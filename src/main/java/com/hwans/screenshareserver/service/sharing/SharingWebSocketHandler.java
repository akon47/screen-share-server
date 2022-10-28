package com.hwans.screenshareserver.service.sharing;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hwans.screenshareserver.common.security.UserAuthenticationDetails;
import com.hwans.screenshareserver.common.security.jwt.JwtTokenProvider;
import com.hwans.screenshareserver.dto.sharing.*;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Component
@RequiredArgsConstructor
public class SharingWebSocketHandler extends TextWebSocketHandler {

    @Getter
    @RequiredArgsConstructor
    private class SharingSession {
        private final WebSocketSession session;
        private final UUID userId;
        private final UUID channelId;

        public boolean sendMessage(PayloadDto payloadDto) {
            try {
                var objectMapper = new ObjectMapper();
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

        if (userSessions.containsKey(userId)) {
            var existSharingSession = userSessions.get(userId);
            if (!existSharingSession.session.getId().equals(session.getId())) {
                existSharingSession.session.close(CloseStatus.SESSION_NOT_RELIABLE);
            }
            userSessions.replace(userId, new SharingSession(session, userId, channelId));
        } else {
            userSessions.put(userId, new SharingSession(session, userId, channelId));
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
}
