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
@ApiModel(description = "dto for websocket kick (host -> server) and kicked notification (server -> target)")
public class KickPayloadDto extends PayloadDto {
    @ApiModelProperty(value = "target user id", required = true)
    UUID userId;
}
