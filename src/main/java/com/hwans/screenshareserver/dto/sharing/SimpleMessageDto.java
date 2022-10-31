package com.hwans.screenshareserver.dto.sharing;

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
@ApiModel(description = "dto for simple message")
public class SimpleMessageDto implements Serializable {
    @ApiModelProperty(value = "id ", required = true)
    @NotNull
    UUID id;
    @ApiModelProperty(value = "message", required = true)
    @NotNull
    String message;
    @ApiModelProperty(value = "author id", required = true)
    @NotNull
    UUID authorId;
    @ApiModelProperty(value = "created at", required = true)
    @NotNull
    LocalDateTime createdAt;
    @ApiModelProperty(value = "last modified at", required = true)
    @NotNull
    LocalDateTime lastModifiedAt;
}
