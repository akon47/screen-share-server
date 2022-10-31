package com.hwans.screenshareserver.controller;

import com.hwans.screenshareserver.common.Constants;
import com.hwans.screenshareserver.common.errors.errorcode.ErrorCodes;
import com.hwans.screenshareserver.common.errors.exception.RestApiException;
import com.hwans.screenshareserver.dto.common.SliceDto;
import com.hwans.screenshareserver.dto.sharing.*;
import com.hwans.screenshareserver.service.authentication.CurrentAuthenticationDetails;
import com.hwans.screenshareserver.service.authentication.UserAuthenticationDetails;
import com.hwans.screenshareserver.service.sharing.SharingService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Optional;
import java.util.UUID;

@RestController
@Api(tags = "Sharing")
@RequestMapping(value = Constants.API_PREFIX + "/v1/sharing")
@RequiredArgsConstructor
public class SharingController {
    private final SharingService sharingService;

    @ApiOperation(value = "create new sharing channel", notes = "create a new sharing channel.")
    @PostMapping(value = "/channels")
    public CreateChannelResponseDto createChannel(@ApiParam(value = "Information for creating a shared channel", required = true) @RequestBody @Valid final CreateChannelRequestDto createChannelRequestDto) {
        return sharingService.createChannel(createChannelRequestDto);
    }

    @ApiOperation(value = "join sharing channel", notes = "join a new sharing channel.")
    @PostMapping(value = "/channels/{channelId}/join")
    public JoinChannelResponseDto joinChannel(@ApiParam(value = "channel id") @PathVariable UUID channelId,
                                              @ApiParam(value = "information for join a shared channel", required = true) @RequestBody @Valid final JoinChannelRequestDto joinChannelRequestDto) {
        return sharingService.joinChannel(channelId, joinChannelRequestDto);
    }

    @ApiOperation(value = "get sharing channel messages", notes = "get messages from sharing channel.")
    @GetMapping(value = "/channels/{channelId}/messages")
    public SliceDto<SimpleMessageDto> getChannelMessages(@ApiParam(value = "channel id") @PathVariable UUID channelId,
                                                         @ApiParam(value = "cursorId for paging") @RequestParam(required = false) Optional<UUID> cursorId,
                                                         @ApiParam(value = "maximum number of pages") @RequestParam(required = false, defaultValue = "20") int size) {
        return sharingService.getMessages(channelId, cursorId, size);
    }

    @ApiOperation(value = "write message to sharing channel", notes = "write message to sharing channel.")
    @PostMapping(value = "/channels/{channelId}/messages")
    public void writeChannelMessages(@CurrentAuthenticationDetails UserAuthenticationDetails userAuthenticationDetails,
                                     @ApiParam(value = "channel id") @PathVariable UUID channelId,
                                     @ApiParam(value = "message data", required = true) @RequestBody @Valid final CreateMessageRequestDto createMessageRequestDto) {
        if (!channelId.equals(userAuthenticationDetails.getChannelId())) {
            throw new RestApiException(ErrorCodes.BadRequest.BAD_REQUEST);
        }

        sharingService.writeMessage(userAuthenticationDetails.getId(), createMessageRequestDto);
    }
}
