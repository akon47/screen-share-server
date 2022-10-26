package com.hwans.screenshareserver.service.sharing;

import com.hwans.screenshareserver.common.Constants;
import com.hwans.screenshareserver.common.security.jwt.JwtStatus;
import com.hwans.screenshareserver.common.security.jwt.JwtTokenProvider;
import com.hwans.screenshareserver.dto.sharing.CreateChannelRequestDto;
import com.hwans.screenshareserver.dto.sharing.CreateChannelResponseDto;
import com.hwans.screenshareserver.dto.sharing.JoinChannelRequestDto;
import com.hwans.screenshareserver.dto.sharing.JoinChannelResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SharingServiceImpl implements SharingService {
    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public CreateChannelResponseDto createChannel(CreateChannelRequestDto createChannelRequestDto) {
        var channelId = UUID.randomUUID();
        var userId = UUID.randomUUID();
        var hostToken = jwtTokenProvider.createHostToken(userId, channelId);
        return CreateChannelResponseDto
                .builder()
                .userId(userId)
                .channelId(channelId)
                .hostToken(hostToken)
                .build();
    }

    @Override
    public JoinChannelResponseDto joinChannel(UUID channelId, JoinChannelRequestDto joinChannelRequestDto) {
        var userId = UUID.randomUUID();
        var guestToken = jwtTokenProvider.createGuestToken(userId, channelId);
        return JoinChannelResponseDto
                .builder()
                    .userId(userId)
                    .channelId(channelId)
                    .guestToken(guestToken)
                .build();
    }
}
