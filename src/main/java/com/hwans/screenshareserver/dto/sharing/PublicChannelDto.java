package com.hwans.screenshareserver.dto.sharing;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Getter;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
@ApiModel(description = "dto for a publicly listed sharing channel")
public class PublicChannelDto implements Serializable {
    @ApiModelProperty(value = "channel id", required = true)
    UUID channelId;
    @ApiModelProperty(value = "channel title", required = false)
    String title;
    @ApiModelProperty(value = "whether the channel requires a password to join", required = true)
    boolean hasPassword;
    @ApiModelProperty(value = "number of users currently in the channel", required = true)
    int userCount;
    @ApiModelProperty(value = "channel creation time", required = true)
    LocalDateTime createdAt;
}
