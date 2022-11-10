package com.hwans.screenshareserver.dto.sharing;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
@NoArgsConstructor
@ApiModel(description = "dto for websocket new message")
public class NewMessagePayloadDto extends PayloadDto {
    @ApiModelProperty(value = "message", required = true)
    SimpleMessageDto message;
}
