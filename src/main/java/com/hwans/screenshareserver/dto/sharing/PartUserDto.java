package com.hwans.screenshareserver.dto.sharing;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.UUID;

@Getter
@SuperBuilder
@NoArgsConstructor
@ApiModel(description = "dto for websocket join channel")
public class PartUserDto extends PayloadDto {
    @ApiModelProperty(value = "user", required = true)
    ChannelUserDto user;
}
