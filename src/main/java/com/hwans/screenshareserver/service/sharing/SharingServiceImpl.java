package com.hwans.screenshareserver.service.sharing;

import com.hwans.screenshareserver.dto.sharing.CreateChannelRequestDto;
import com.hwans.screenshareserver.dto.sharing.CreateChannelResponseDto;
import com.hwans.screenshareserver.dto.sharing.JoinChannelRequestDto;
import com.hwans.screenshareserver.dto.sharing.JoinChannelResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SharingServiceImpl implements SharingService {
    @Override
    public CreateChannelResponseDto createChannel(CreateChannelRequestDto createChannelRequestDto) {
        return CreateChannelResponseDto.builder().id("id").ownerToken("owner-token").build();
    }

    @Override
    public JoinChannelResponseDto joinChannel(UUID channelId, JoinChannelRequestDto joinChannelRequestDto) {
        return JoinChannelResponseDto.builder().guestToken("guest-token").build();
    }
}
