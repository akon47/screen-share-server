package com.hwans.screenshareserver.dto.sharing;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
@Getter
@SuperBuilder
@NoArgsConstructor
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXISTING_PROPERTY, property = "type", visible = true)
@JsonSubTypes({
        @JsonSubTypes.Type(value = PayloadDto.class, name = "JOIN_CHANNEL"),
        @JsonSubTypes.Type(value = PayloadDto.class, name = "PART_CHANNEL"),
        @JsonSubTypes.Type(value = RelaySessionDescriptionDto.class, name = "RELAY_SESSION_DESCRIPTION"),
        @JsonSubTypes.Type(value = RelayIceCandidateDto.class, name = "RELAY_ICE_CANDIDATE"),
        @JsonSubTypes.Type(value = JoinUserDto.class, name = "JOIN_USER"),
        @JsonSubTypes.Type(value = PartUserDto.class, name = "PART_USER")
})
@ApiModel(description = "dto for websocket")
public class PayloadDto implements Serializable {
    @ApiModelProperty(value = "authorization token")
    String authorizationToken;
    @ApiModelProperty(value = "payload type", required = true)
    PayloadType type;
}

