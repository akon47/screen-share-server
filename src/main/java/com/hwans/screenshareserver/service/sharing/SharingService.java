package com.hwans.screenshareserver.service.sharing;

import com.hwans.screenshareserver.dto.sharing.CreateSharingChannelRequestDto;
import com.hwans.screenshareserver.dto.sharing.CreateSharingChannelResponseDto;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;

public interface SharingService {
    @Validated
    CreateSharingChannelResponseDto createSharingChannel(@Valid CreateSharingChannelRequestDto createSharingChannelRequestDto);
}
