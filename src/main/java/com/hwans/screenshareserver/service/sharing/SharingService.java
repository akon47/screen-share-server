package com.hwans.screenshareserver.service.sharing;

import com.hwans.screenshareserver.dto.common.SliceDto;
import com.hwans.screenshareserver.dto.sharing.*;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.Optional;
import java.util.UUID;

public interface SharingService {
    @Validated
    CreateChannelResponseDto createChannel(@Valid CreateChannelRequestDto createChannelRequestDto);

    @Validated
    JoinChannelResponseDto joinChannel(@Valid @NotNull UUID channelId, @Valid JoinChannelRequestDto joinChannelRequestDto);

    @Validated
    void writeMessage(@Valid @NotNull UUID authorUserId, @Valid CreateMessageRequestDto createMessageRequestDto);

    SliceDto<SimpleMessageDto> getMessages(@Valid @NotNull UUID channelId, Optional<UUID> cursorId, int size);
}
