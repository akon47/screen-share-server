package com.hwans.screenshareserver.service.sharing;

import com.hwans.screenshareserver.dto.sharing.CreateSharingChannelRequestDto;
import com.hwans.screenshareserver.dto.sharing.CreateSharingChannelResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SharingServiceImpl implements SharingService {
    @Override
    public CreateSharingChannelResponseDto createSharingChannel(CreateSharingChannelRequestDto createSharingChannelRequestDto) {
        return null;
    }
}
