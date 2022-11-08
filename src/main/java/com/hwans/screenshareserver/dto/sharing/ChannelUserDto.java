package com.hwans.screenshareserver.dto.sharing;

import com.hwans.screenshareserver.common.security.RoleType;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Getter;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
@ApiModel(description = "dto for channel user")
public class ChannelUserDto implements Serializable {
    @ApiModelProperty(value = "id ", required = true)
    @NotNull
    UUID id;
    @ApiModelProperty(value = "role type", required = true)
    @NotNull
    RoleType roleType;
}
