package com.hwans.screenshareserver.dto.sharing;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Getter
@ApiModel(description = "Dto for creating sharing channel")
public class CreateChannelRequestDto implements Serializable {
    @ApiModelProperty(value = "channel password", required = false)
    String password;
}
