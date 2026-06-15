package com.hwans.screenshareserver.dto.sharing;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Getter
@ApiModel(description = "dto for creating sharing channel")
public class CreateChannelRequestDto implements Serializable {
    @ApiModelProperty(value = "channel password", required = false)
    String password;
    @ApiModelProperty(value = "channel title (shown in the public channel list)", required = false)
    String title;
    // Field is named publicChannel (not isPublic) to avoid the boolean "is"
    // prefix ambiguity in Jackson/MapStruct; the JSON contract stays "isPublic".
    @ApiModelProperty(value = "whether the channel is listed publicly", required = false)
    @JsonProperty("isPublic")
    boolean publicChannel;
}
