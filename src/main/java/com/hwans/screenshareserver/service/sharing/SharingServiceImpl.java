package com.hwans.screenshareserver.service.sharing;

import com.hwans.screenshareserver.common.Constants;
import com.hwans.screenshareserver.common.errors.errorcode.ErrorCodes;
import com.hwans.screenshareserver.common.errors.exception.RestApiException;
import com.hwans.screenshareserver.common.security.RoleType;
import com.hwans.screenshareserver.common.security.jwt.JwtStatus;
import com.hwans.screenshareserver.common.security.jwt.JwtTokenProvider;
import com.hwans.screenshareserver.dto.common.SliceDto;
import com.hwans.screenshareserver.dto.sharing.*;
import com.hwans.screenshareserver.entity.sharing.SharingChannel;
import com.hwans.screenshareserver.entity.sharing.SharingMessage;
import com.hwans.screenshareserver.entity.sharing.SharingUser;
import com.hwans.screenshareserver.mapper.SharingMapper;
import com.hwans.screenshareserver.repository.sharing.SharingChannelRepository;
import com.hwans.screenshareserver.repository.sharing.SharingMessageRepository;
import com.hwans.screenshareserver.repository.sharing.SharingUserRepository;
import com.hwans.screenshareserver.service.authentication.UserAuthenticationDetails;
import com.nimbusds.oauth2.sdk.util.StringUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.PageRequest;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SharingServiceImpl implements SharingService, UserDetailsService {
    private final SharingChannelRepository sharingChannelRepository;
    private final SharingUserRepository sharingUserRepository;
    private final SharingMessageRepository sharingMessageRepository;
    private final SharingMapper sharingMapper;
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;
    private final SharingWebSocketHandler sharingWebSocketHandler;

    @Override
    @Transactional
    public CreateChannelResponseDto createChannel(CreateChannelRequestDto createChannelRequestDto) {
        var savedChannel = sharingChannelRepository.save(sharingMapper.toEntity(createChannelRequestDto));
        var savedUser = sharingUserRepository.save(SharingUser.builder().role(RoleType.HOST.getName()).channel(savedChannel).build());

        savedChannel.setHost(savedUser);
        savedUser.setChannel(savedChannel);
        savedUser.setToken(jwtTokenProvider.createHostToken(savedUser.getId(), savedChannel.getId()));

        return CreateChannelResponseDto
                .builder()
                .userId(savedUser.getId())
                .channelId(savedChannel.getId())
                .hostToken(savedUser.getToken())
                .build();
    }

    @Override
    @Transactional
    public JoinChannelResponseDto joinChannel(UUID channelId, JoinChannelRequestDto joinChannelRequestDto) {
        var channel = sharingChannelRepository
                .findById(channelId)
                .orElseThrow(() -> new RestApiException(ErrorCodes.NotFound.NOT_FOUND));

        if (!passwordEncoder.matches(joinChannelRequestDto.getPassword(), channel.getPassword())) {
            throw new RestApiException(ErrorCodes.Unauthorized.UNAUTHORIZED);
        }

        var savedGuestUser = sharingUserRepository.save(SharingUser
                .builder()
                .role(RoleType.GUEST.getName())
                .token(jwtTokenProvider.createGuestToken(UUID.randomUUID(), channelId))
                .channel(channel)
                .build());

        return JoinChannelResponseDto
                .builder()
                .userId(savedGuestUser.getId())
                .channelId(savedGuestUser.getChannel().getId())
                .guestToken(savedGuestUser.getToken())
                .build();
    }

    @Override
    @Transactional
    public SimpleMessageDto writeMessage(UUID authorUserId, CreateMessageRequestDto createMessageRequestDto) {
        var authorUser = sharingUserRepository
                .findById(authorUserId)
                .orElseThrow(() -> new RestApiException(ErrorCodes.NotFound.NOT_FOUND));

        var savedMessage = sharingMessageRepository.save(SharingMessage
                .builder()
                .message(createMessageRequestDto.getMessage())
                .author(authorUser)
                .build());
        var simpleMessageDto = sharingMapper.entityToSimpleMessageDto(savedMessage);
        sharingWebSocketHandler.broadcastNewMessage(authorUser.getChannel().getId(), simpleMessageDto);
        return simpleMessageDto;
    }

    @Override
    public SliceDto<SimpleMessageDto> getMessages(UUID channelId, Optional<UUID> cursorId, int size) {
        List<SharingMessage> foundMessages;
        if (cursorId.isPresent()) {
            var foundCursorMessage = sharingMessageRepository
                    .findById(cursorId.get())
                    .orElseThrow(() -> new RestApiException(ErrorCodes.NotFound.NOT_FOUND));
            foundMessages = sharingMessageRepository.findByIdLessThanOrderByIdDesc(channelId, foundCursorMessage.getId(), foundCursorMessage.getCreatedAt(), PageRequest.of(0, size + 1));
        } else {
            foundMessages = sharingMessageRepository.findAllByOrderByIdDesc(channelId, PageRequest.of(0, size + 1));
        }
        var last = foundMessages.size() <= size;
        return SliceDto.<SimpleMessageDto>builder()
                .data(foundMessages.stream().limit(size).map(sharingMapper::entityToSimpleMessageDto).toList())
                .size((int) foundMessages.stream().limit(size).count())
                .empty(foundMessages.isEmpty())
                .first(cursorId.isEmpty())
                .last(last)
                .cursorId(last ? null : foundMessages.stream().limit(size).skip(size - 1).findFirst().map(SharingMessage::getId).orElse(null))
                .build();
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return sharingUserRepository
                .findById(UUID.fromString(username))
                .map(this::createAuthenticationDetails)
                .orElseThrow(() -> new RestApiException(ErrorCodes.NotFound.NOT_FOUND));
    }

    private UserAuthenticationDetails createAuthenticationDetails(SharingUser sharingUser) {
        var authorities =
                Collections.singletonList(new SimpleGrantedAuthority(sharingUser.getRole()));
        return new UserAuthenticationDetails(
                sharingUser.getId(),
                sharingUser.getChannel().getId(),
                authorities);
    }

    @Bean
    public Function<UserDetails, UserAuthenticationDetails> fetchCurrentUserAuthenticationDetails() {
        return (principal -> {
            if (principal == null)
                throw new RestApiException(ErrorCodes.Unauthorized.UNAUTHORIZED);

            UUID userId = UUID.fromString(principal.getUsername());
            return sharingUserRepository
                    .findById(userId)
                    .map(this::createAuthenticationDetails)
                    .orElseThrow(() -> new RestApiException(ErrorCodes.NotFound.NOT_FOUND));
        });
    }
}
