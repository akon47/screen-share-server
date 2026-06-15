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
@ApiModel(description = "dto for websocket emoji reaction")
public class ReactionPayloadDto extends PayloadDto {
    @ApiModelProperty(value = "emoji", required = true)
    String emoji;
    @ApiModelProperty(value = "reaction sender user id")
    UUID userId;
}
