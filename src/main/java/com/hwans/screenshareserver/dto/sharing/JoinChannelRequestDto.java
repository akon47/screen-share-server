package com.hwans.screenshareserver.dto.sharing;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.io.Serializable;

@Getter
@ApiModel(description = "dto for join sharing channel")
public class JoinChannelRequestDto implements Serializable {
    @ApiModelProperty(value = "channel password", required = false)
    String password;
}
