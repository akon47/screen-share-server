package com.hwans.screenshareserver.service.sharing;

import com.hwans.screenshareserver.dto.sharing.CreateChannelRequestDto;
import com.hwans.screenshareserver.dto.sharing.CreateChannelResponseDto;
import com.hwans.screenshareserver.dto.sharing.JoinChannelRequestDto;
import com.hwans.screenshareserver.dto.sharing.JoinChannelResponseDto;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.UUID;

public interface SharingService {
    @Validated
    CreateChannelResponseDto createChannel(@Valid CreateChannelRequestDto createChannelRequestDto);

    @Validated
    JoinChannelResponseDto joinChannel(@Valid @NotNull UUID channelId, @Valid JoinChannelRequestDto joinChannelRequestDto);
}
