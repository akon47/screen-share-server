package com.hwans.screenshareserver.dto.sharing;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@ApiModel(description = "WebRTC ICE server entry (STUN/TURN)")
public class IceServerDto implements Serializable {
    @ApiModelProperty(value = "ICE server urls")
    List<String> urls;
    @ApiModelProperty(value = "TURN username (short-lived)")
    String username;
    @ApiModelProperty(value = "TURN credential (short-lived)")
    String credential;
}
