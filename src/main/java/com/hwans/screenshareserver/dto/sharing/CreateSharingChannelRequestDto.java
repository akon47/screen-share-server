package com.hwans.screenshareserver.dto.sharing;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Getter;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Getter
@Builder
@ApiModel(description = "Dto for creating sharing channel")
public class CreateSharingChannelRequestDto implements Serializable {
    @ApiModelProperty(value = "channel password", required = false)
    String password;
    @ApiModelProperty(value = "sdp blob for offer", required = true)
    @NotNull
    Object sdp;
}
