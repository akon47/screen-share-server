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
@ApiModel(description = "dto for presenter signaling: REQUEST_PRESENT, SET_PRESENTER, PRESENTER_CHANGED")
public class PresenterPayloadDto extends PayloadDto {
    @ApiModelProperty(value = "target / presenter user id (null in SET_PRESENTER means release to host)")
    UUID userId;
}
