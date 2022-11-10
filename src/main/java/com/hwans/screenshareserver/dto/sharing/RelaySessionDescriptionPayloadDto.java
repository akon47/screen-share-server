package com.hwans.screenshareserver.dto.sharing;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.UUID;

@Getter
@SuperBuilder
@NoArgsConstructor
@ApiModel(description = "dto for websocket relay session description")
public class RelaySessionDescriptionPayloadDto extends PayloadDto {
    @ApiModelProperty(value = "relay target user id", required = true)
    UUID userId;
    @ApiModelProperty(value = "session description", required = true)
    Object sessionDescription;
}
