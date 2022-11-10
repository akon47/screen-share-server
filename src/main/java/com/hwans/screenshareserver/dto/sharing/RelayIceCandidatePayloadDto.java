package com.hwans.screenshareserver.dto.sharing;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.UUID;

@Getter
@SuperBuilder
@NoArgsConstructor
@ApiModel(description = "dto for websocket relay ice candidate")
public class RelayIceCandidatePayloadDto extends PayloadDto {
    @ApiModelProperty(value = "relay target user id", required = true)
    UUID userId;
    @ApiModelProperty(value = "ice candidate", required = true)
    Object iceCandidate;
}
