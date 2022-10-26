package com.hwans.screenshareserver.service.sharing;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hwans.screenshareserver.common.security.UserAuthenticationDetails;
import com.hwans.screenshareserver.common.security.jwt.JwtTokenProvider;
import com.hwans.screenshareserver.dto.sharing.PayloadDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import springfox.documentation.spring.web.json.Json;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Component
@RequiredArgsConstructor
public class SharingWebSocketHandler extends TextWebSocketHandler {
    private final JwtTokenProvider jwtTokenProvider;
    private Map<String, WebSocketSession> sessions = new ConcurrentHashMap<String, WebSocketSession>();
    private Map<UUID, WebSocketSession> channels = new ConcurrentHashMap<UUID, WebSocketSession>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        sessions.put(session.getId(), session);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        sessions.remove(session.getId());
    }

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) throws JsonProcessingException {
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
                OnRelaySessionDescription(session, userAuthenticationDetails);
            }
            case RELAY_ICE_CANDIDATE -> {
                OnRelayIceCandidate(session, userAuthenticationDetails);
            }
        }
    }

    private void OnJoinChannel(WebSocketSession session, UserAuthenticationDetails userAuthenticationDetails)
    {

    }

    private void OnPartChannel(WebSocketSession session, UserAuthenticationDetails userAuthenticationDetails)
    {

    }

    private void OnRelaySessionDescription(WebSocketSession session, UserAuthenticationDetails userAuthenticationDetails)
    {

    }

    private void OnRelayIceCandidate(WebSocketSession session, UserAuthenticationDetails userAuthenticationDetails)
    {

    }
}
