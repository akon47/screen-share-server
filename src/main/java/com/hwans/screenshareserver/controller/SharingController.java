package com.hwans.screenshareserver.controller;

import com.hwans.screenshareserver.common.Constants;
import com.hwans.screenshareserver.dto.sharing.CreateSharingChannelRequestDto;
import com.hwans.screenshareserver.dto.sharing.CreateSharingChannelResponseDto;
import com.hwans.screenshareserver.service.sharing.SharingService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@Api(tags = "Sharing")
@RequestMapping(value = Constants.API_PREFIX + "/v1/sharing")
@RequiredArgsConstructor
public class SharingController {
    private final SharingService sharingService;

    @ApiOperation(value = "Create New Sharing Channel", notes = "Create a new sharing channel.")
    @PostMapping(value = "channels")
    public CreateSharingChannelResponseDto createSharingChannel(@ApiParam(value = "Information for creating a shared channel", required = true) @RequestBody @Valid final CreateSharingChannelRequestDto createSharingChannelRequestDto) {
        return sharingService.createSharingChannel(createSharingChannelRequestDto);
    }
}
