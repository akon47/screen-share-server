package com.hwans.screenshareserver.dto.sharing;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Getter;

import java.io.Serializable;

@Getter
@Builder
@ApiModel(description = "Dto for creating answer")
public class AnswerDto implements Serializable {
    @ApiModelProperty(value = "answer sdp", required = true)
    Object answer;
}
