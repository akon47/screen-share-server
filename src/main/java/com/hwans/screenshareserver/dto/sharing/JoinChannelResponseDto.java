package com.hwans.screenshareserver.dto.sharing;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Getter;

import java.io.Serializable;
import java.util.UUID;

@Getter
@Builder
@ApiModel(description = "Dto for join sharing channel response")
public class JoinChannelResponseDto implements Serializable {
    @ApiModelProperty(value = "channel id", required = true, accessMode = ApiModelProperty.AccessMode.READ_ONLY)
    UUID id;
    @ApiModelProperty(value = "channel guest token", required = true, accessMode = ApiModelProperty.AccessMode.READ_ONLY)
    String guestToken;
}
