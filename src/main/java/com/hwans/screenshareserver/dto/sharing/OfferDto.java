package com.hwans.screenshareserver.dto.sharing;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Getter;

import java.io.Serializable;

@Getter
@Builder
@ApiModel(description = "Dto for creating offer")
public class OfferDto implements Serializable {
    @ApiModelProperty(value = "offer sdp", required = true)
    Object offer;
}
