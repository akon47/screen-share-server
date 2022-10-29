package com.hwans.screenshareserver.service.sharing;

import com.hwans.screenshareserver.common.Constants;
import com.hwans.screenshareserver.common.errors.errorcode.ErrorCodes;
import com.hwans.screenshareserver.common.errors.exception.RestApiException;
import com.hwans.screenshareserver.common.security.RoleType;
import com.hwans.screenshareserver.common.security.jwt.JwtStatus;
import com.hwans.screenshareserver.common.security.jwt.JwtTokenProvider;
import com.hwans.screenshareserver.dto.sharing.CreateChannelRequestDto;
import com.hwans.screenshareserver.dto.sharing.CreateChannelResponseDto;
import com.hwans.screenshareserver.dto.sharing.JoinChannelRequestDto;
import com.hwans.screenshareserver.dto.sharing.JoinChannelResponseDto;
import com.hwans.screenshareserver.entity.sharing.SharingChannel;
import com.hwans.screenshareserver.entity.sharing.SharingUser;
import com.hwans.screenshareserver.mapper.SharingMapper;
import com.hwans.screenshareserver.repository.sharing.SharingChannelRepository;
import com.hwans.screenshareserver.repository.sharing.SharingUserRepository;
import com.nimbusds.oauth2.sdk.util.StringUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SharingServiceImpl implements SharingService {
    private final SharingChannelRepository sharingChannelRepository;
    private final SharingUserRepository sharingUserRepository;
    private final SharingMapper sharingMapper;
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public CreateChannelResponseDto createChannel(CreateChannelRequestDto createChannelRequestDto) {
        var savedChannel = sharingChannelRepository.save(sharingMapper.toEntity(createChannelRequestDto));
        var savedUser = sharingUserRepository.save(SharingUser.builder().role(RoleType.HOST.toString()).channel(savedChannel).build());

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
                .role(RoleType.GUEST.toString())
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
}
