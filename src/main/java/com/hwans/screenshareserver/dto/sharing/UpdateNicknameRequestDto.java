package com.hwans.screenshareserver.dto.sharing;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;

import javax.validation.constraints.Size;
import java.io.Serializable;

@Getter
@ApiModel(description = "dto for updating the current user's nickname")
public class UpdateNicknameRequestDto implements Serializable {
    @ApiModelProperty(value = "nickname (max 30 chars, blank to clear)")
    @Size(max = 30)
    String nickname;
}
