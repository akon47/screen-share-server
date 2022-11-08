package com.hwans.screenshareserver.service.sharing;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.hwans.screenshareserver.common.security.RoleType;
import com.hwans.screenshareserver.dto.common.CollectionDto;
import com.hwans.screenshareserver.service.authentication.UserAuthenticationDetails;
import com.hwans.screenshareserver.common.security.jwt.JwtTokenProvider;
import com.hwans.screenshareserver.dto.sharing.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class SharingWebSocketHandler extends TextWebSocketHandler {

    @Getter
    @RequiredArgsConstructor
    private class SharingSession {
        private final WebSocketSession session;
        private final UUID userId;
        private final UUID channelId;
        private final RoleType roleType;

        public boolean sendMessage(PayloadDto payloadDto) {
            try {
                var objectMapper = new ObjectMapper();
                objectMapper.registerModule(new JavaTimeModule());
                objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
                this.session.sendMessage(new TextMessage(objectMapper.writeValueAsString(payloadDto)));
                return true;
            } catch (IOException e) {
                return false;
            }
        }
    }

    private final JwtTokenProvider jwtTokenProvider;
    private HashMap<String, UUID> sessions = new HashMap<>();
    private HashMap<UUID, SharingSession> userSessions = new HashMap<>();
    private HashMap<UUID, HashSet<UUID>> channels = new HashMap<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {

    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        var userId = sessions.get(session.getId());
        if (userId == null) {
            return;
        }
        sessions.remove(userId);

        var sharingSession = userSessions.get(userId);
        if (sharingSession == null) {
            return;
        }
        userSessions.remove(userId);

        var channelUsers = channels.get(sharingSession.getChannelId());
        if (channelUsers == null) {
            return;
        }

        channelUsers.remove(userId);

        channelUsers.stream()
                .map(x -> userSessions.getOrDefault(x, null))
                .filter(x -> x != null && x.getUserId() != userId)
                .forEach(x -> x.sendMessage(JoinUserDto.builder().type(PayloadType.PART_USER).userId(userId).build()));
    }

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) throws IOException {
        var objectMapper = new ObjectMapper();
        var payload = objectMapper.readValue(message.getPayload(), PayloadDto.class);
        var userAuthenticationDetails = jwtTokenProvider.getUserAuthenticationDetails(payload.getAuthorizationToken());

        switch (payload.getType()) {
            case JOIN_CHANNEL -> {
                OnJoinChannel(session, userAuthenticationDetails);
            }
            case PART_CHANNEL -> {
                OnPartChannel(session, userAuthenticationDetails);
            }
            case RELAY_SESSION_DESCRIPTION -> {
                OnRelaySessionDescription(session, userAuthenticationDetails, (RelaySessionDescriptionDto) payload);
            }
            case RELAY_ICE_CANDIDATE -> {
                OnRelayIceCandidate(session, userAuthenticationDetails, (RelayIceCandidateDto) payload);
            }
        }
    }

    private void OnJoinChannel(WebSocketSession session, UserAuthenticationDetails userAuthenticationDetails) throws IOException {
        var userId = userAuthenticationDetails.getId();
        var channelId = userAuthenticationDetails.getChannelId();
        var roleType = userAuthenticationDetails.getAuthorities().stream().map(x -> RoleType.fromName(x.getAuthority())).findFirst().orElse(RoleType.UNKNOWN);

        if (userSessions.containsKey(userId)) {
            var existSharingSession = userSessions.get(userId);
            if (!existSharingSession.session.getId().equals(session.getId())) {
                existSharingSession.session.close(CloseStatus.SESSION_NOT_RELIABLE);
            }
            userSessions.replace(userId, new SharingSession(session, userId, channelId, roleType));
        } else {
            userSessions.put(userId, new SharingSession(session, userId, channelId, roleType));
        }

        sessions.put(session.getId(), userId);

        channels.putIfAbsent(channelId, new HashSet<>());

        var channelUsers = channels.get(channelId);
        channelUsers.add(userId);
        channelUsers.stream()
                .map(x -> userSessions.getOrDefault(x, null))
                .filter(x -> x != null && x.getUserId() != userId)
                .forEach(x -> x.sendMessage(JoinUserDto.builder().type(PayloadType.JOIN_USER).userId(userId).build()));
    }

    private void OnPartChannel(WebSocketSession session, UserAuthenticationDetails userAuthenticationDetails) {

    }

    private void OnRelaySessionDescription(WebSocketSession session, UserAuthenticationDetails userAuthenticationDetails, RelaySessionDescriptionDto relaySessionDescriptionDto) {
        var userId = userAuthenticationDetails.getId();
        var channelId = userAuthenticationDetails.getChannelId();

        var channelUsers = channels.get(channelId);
        if (channelUsers == null || !channelUsers.contains(relaySessionDescriptionDto.getUserId())) {
            return;
        }

        userSessions.get(relaySessionDescriptionDto.getUserId())
                .sendMessage(RelaySessionDescriptionDto
                        .builder()
                        .type(PayloadType.RELAY_SESSION_DESCRIPTION)
                        .userId(userId)
                        .sessionDescription(relaySessionDescriptionDto.getSessionDescription())
                        .build());
    }

    private void OnRelayIceCandidate(WebSocketSession session, UserAuthenticationDetails userAuthenticationDetails, RelayIceCandidateDto relayIceCandidateDto) {
        var userId = userAuthenticationDetails.getId();
        var channelId = userAuthenticationDetails.getChannelId();

        var channelUsers = channels.get(channelId);
        if (channelUsers == null || !channelUsers.contains(relayIceCandidateDto.getUserId())) {
            return;
        }

        userSessions.get(relayIceCandidateDto.getUserId())
                .sendMessage(RelayIceCandidateDto
                        .builder()
                        .type(PayloadType.RELAY_ICE_CANDIDATE)
                        .userId(userId)
                        .iceCandidate(relayIceCandidateDto.getIceCandidate())
                        .build());
    }

    public void broadcastNewMessage(UUID channelId, SimpleMessageDto simpleMessageDto) {
        var channelUsers = channels.get(channelId);
        if (channelUsers == null) {
            return;
        }

        var newMessagePayload = NewMessageDto.builder().type(PayloadType.NEW_MESSAGE).message(simpleMessageDto).build();
        channelUsers.stream()
                .map(x -> userSessions.get(x))
                .forEach(x -> x.sendMessage(newMessagePayload));
    }

    public CollectionDto<ChannelUserDto> getChannelUsers(UUID channelId) {
        var channelUsers = channels.get(channelId);
        if (channelUsers == null) {
            return null;
        }

        var channelUserDtoList = channelUsers.stream()
                .map(x -> userSessions.get(x))
                .map(x -> ChannelUserDto.builder().id(x.getUserId()).roleType(x.getRoleType()).build())
                .toList();

        return CollectionDto.<ChannelUserDto>builder()
                .data(channelUserDtoList)
                .size(channelUserDtoList.size())
                .build();
    }
}
