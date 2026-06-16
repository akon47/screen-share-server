package com.hwans.screenshareserver.dto.sharing;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.List;
import java.util.UUID;

@Getter
@SuperBuilder
@NoArgsConstructor
@ApiModel(description = "dto for websocket host drawing annotation")
public class DrawPayloadDto extends PayloadDto {
    @ApiModelProperty(value = "stroke id (groups the points of a single pen stroke)")
    String strokeId;
    @ApiModelProperty(value = "draw mode: pen or eraser")
    String mode;
    @ApiModelProperty(value = "stroke color (CSS color)")
    String color;
    @ApiModelProperty(value = "stroke width, normalized to the video content height")
    Double width;
    @ApiModelProperty(value = "flattened normalized points [x0, y0, x1, y1, ...] in the range 0..1")
    List<Double> points;
    @ApiModelProperty(value = "id of the user (host) who drew")
    UUID userId;
    @ApiModelProperty(value = "when set, deliver only to this user (used to replay existing strokes to a new joiner)")
    UUID targetUserId;
}
