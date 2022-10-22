package com.hwans.screenshareserver.controller;

import com.hwans.screenshareserver.common.Constants;
import com.hwans.screenshareserver.dto.sharing.CreateChannelRequestDto;
import com.hwans.screenshareserver.dto.sharing.CreateChannelResponseDto;
import com.hwans.screenshareserver.dto.sharing.JoinChannelRequestDto;
import com.hwans.screenshareserver.dto.sharing.JoinChannelResponseDto;
import com.hwans.screenshareserver.service.sharing.SharingService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.UUID;

@RestController
@Api(tags = "Sharing")
@RequestMapping(value = Constants.API_PREFIX + "/v1/sharing")
@RequiredArgsConstructor
public class SharingController {
    private final SharingService sharingService;

    @ApiOperation(value = "create new sharing channel", notes = "Create a new sharing channel.")
    @PostMapping(value = "/channels")
    public CreateChannelResponseDto createChannel(@ApiParam(value = "Information for creating a shared channel", required = true) @RequestBody @Valid final CreateChannelRequestDto createChannelRequestDto) {
        return sharingService.createChannel(createChannelRequestDto);
    }

    @ApiOperation(value = "join sharing channel", notes = "join a new sharing channel.")
    @PostMapping(value = "/channels/{channelId}/join")
    public JoinChannelResponseDto joinChannel(@ApiParam(value = "channel id") @PathVariable UUID channelId,
                                              @ApiParam(value = "Information for join a shared channel", required = true) @RequestBody @Valid final JoinChannelRequestDto joinChannelRequestDto) {
        return sharingService.joinChannel(channelId, joinChannelRequestDto);
    }
}
