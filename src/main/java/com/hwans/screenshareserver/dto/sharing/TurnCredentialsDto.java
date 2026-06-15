package com.hwans.screenshareserver.dto.sharing;

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
@ApiModel(description = "short-lived ICE servers (STUN/TURN) for a WebRTC session")
public class TurnCredentialsDto implements Serializable {
    @ApiModelProperty(value = "ICE servers to use in RTCPeerConnection configuration")
    List<IceServerDto> iceServers;
}
