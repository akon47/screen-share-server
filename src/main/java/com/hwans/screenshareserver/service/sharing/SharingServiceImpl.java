package com.hwans.screenshareserver.service.sharing;

import com.hwans.screenshareserver.common.security.jwt.JwtTokenProvider;
import com.hwans.screenshareserver.dto.sharing.CreateChannelRequestDto;
import com.hwans.screenshareserver.dto.sharing.CreateChannelResponseDto;
import com.hwans.screenshareserver.dto.sharing.JoinChannelRequestDto;
import com.hwans.screenshareserver.dto.sharing.JoinChannelResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SharingServiceImpl extends TextWebSocketHandler implements SharingService {
    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public CreateChannelResponseDto createChannel(CreateChannelRequestDto createChannelRequestDto) {
        var channelId = UUID.randomUUID();
        return CreateChannelResponseDto.builder().id(channelId).hostToken(jwtTokenProvider.createHostToken(channelId)).build();
    }

    @Override
    public JoinChannelResponseDto joinChannel(UUID channelId, JoinChannelRequestDto joinChannelRequestDto) {
        return JoinChannelResponseDto.builder().id(channelId).guestToken(jwtTokenProvider.createGuestToken(channelId)).build();
    }
}
