package com.hwans.screenshareserver.dto.sharing;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Getter;

import java.io.Serializable;

@Getter
@Builder
@ApiModel(description = "Dto for creating sharing channel response")
public class CreateChannelResponseDto implements Serializable {
    @ApiModelProperty(value = "channel id", required = true, accessMode = ApiModelProperty.AccessMode.READ_ONLY)
    String id;
    @ApiModelProperty(value = "channel owner token", required = true, accessMode = ApiModelProperty.AccessMode.READ_ONLY)
    String ownerToken;
}
