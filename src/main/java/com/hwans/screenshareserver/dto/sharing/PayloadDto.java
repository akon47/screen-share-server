package com.hwans.screenshareserver.dto.sharing;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Getter;

import java.io.Serializable;

@Getter
@ApiModel(description = "Dto for websocket")
public class PayloadDto implements Serializable {
    @ApiModelProperty(value = "authorization token", required = true)
    String authorizationToken;
    @ApiModelProperty(value = "payload type", required = true)
    PayloadType type;
    @ApiModelProperty(value = "payload data", required = true)
    Object data;
}
