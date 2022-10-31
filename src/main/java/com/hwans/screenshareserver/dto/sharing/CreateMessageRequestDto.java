package com.hwans.screenshareserver.dto.sharing;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Getter
@ApiModel(description = "dto for write message")
public class CreateMessageRequestDto implements Serializable {
    @ApiModelProperty(value = "message", required = true)
    @NotNull
    String message;
}
