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
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

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

        public ChannelUserDto toChannelUserDto() {
            return ChannelUserDto.builder()
                    .id(this.userId)
                    .roleType(this.roleType)
                    .build();
        }
    }

    private final JwtTokenProvider jwtTokenProvider;
    private final Map<String, UUID> sessions = new ConcurrentHashMap<>();
    private final Map<UUID, SharingSession> userSessions = new ConcurrentHashMap<>();
    private final Map<UUID, HashSet<UUID>> channels = new ConcurrentHashMap<>();
    // The user currently presenting (sending media) in each channel.
    private final Map<UUID, UUID> channelPresenters = new ConcurrentHashMap<>();

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

        var partedChannelUserDto = ChannelUserDto.builder().id(sharingSession.getUserId()).roleType(sharingSession.getRoleType()).build();
        channelUsers.stream()
                .map(x -> userSessions.getOrDefault(x, null))
                .filter(x -> x != null && x.getUserId() != userId)
                .forEach(x -> x.sendMessage(ChannelUserPayloadDto.builder().type(PayloadType.PART_USER).user(partedChannelUserDto).build()));

        // If the user who left was presenting, hand presenting back to the host.
        var channelId = sharingSession.getChannelId();
        if (userId.equals(channelPresenters.get(channelId))) {
            var newPresenter = findHostUserId(channelId);
            if (newPresenter != null) {
                channelPresenters.put(channelId, newPresenter);
                broadcastToChannel(channelId, PresenterPayloadDto.builder().type(PayloadType.PRESENTER_CHANGED).userId(newPresenter).build(), null);
            } else {
                channelPresenters.remove(channelId);
            }
        }
        if (channelUsers.isEmpty()) {
            channelPresenters.remove(channelId);
        }
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
                OnRelaySessionDescription(session, userAuthenticationDetails, (RelaySessionDescriptionPayloadDto) payload);
            }
            case RELAY_ICE_CANDIDATE -> {
                OnRelayIceCandidate(session, userAuthenticationDetails, (RelayIceCandidatePayloadDto) payload);
            }
            case REACTION -> {
                OnReaction(userAuthenticationDetails, (ReactionPayloadDto) payload);
            }
            case KICK -> {
                OnKick(userAuthenticationDetails, (KickPayloadDto) payload);
            }
            case REQUEST_PRESENT -> {
                OnRequestPresent(userAuthenticationDetails);
            }
            case SET_PRESENTER -> {
                OnSetPresenter(userAuthenticationDetails, (PresenterPayloadDto) payload);
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

        var joinedUserSession = userSessions.get(userId);
        var joinedChannelUserDto = joinedUserSession.toChannelUserDto();

        // The host is the default presenter for the channel.
        if (roleType == RoleType.HOST) {
            channelPresenters.putIfAbsent(channelId, userId);
        }

        var channelUsers = channels.get(channelId);
        if (channelUsers.add(userId)) {
            channelUsers.stream()
                    .map(x -> userSessions.getOrDefault(x, null))
                    .filter(x -> x != null && x.getUserId() != userId)
                    .forEach(x -> x.sendMessage(ChannelUserPayloadDto.builder().type(PayloadType.JOIN_USER).user(joinedChannelUserDto).build()));
            joinedUserSession.sendMessage(ChannelUserPayloadDto.builder().type(PayloadType.CHANNEL_JOINED).user(joinedChannelUserDto).build());

            // Tell the joiner who is currently presenting so they render the right UI.
            var presenter = channelPresenters.get(channelId);
            if (presenter != null) {
                joinedUserSession.sendMessage(PresenterPayloadDto.builder().type(PayloadType.PRESENTER_CHANGED).userId(presenter).build());
            }
        }
    }

    private void OnPartChannel(WebSocketSession session, UserAuthenticationDetails userAuthenticationDetails) {
        var userId = sessions.get(session.getId());
        if (userId == null) {
            return;
        }

        var sharingSession = userSessions.get(userId);
        if (sharingSession == null) {
            return;
        }

        var channelUsers = channels.get(sharingSession.getChannelId());
        if (channelUsers == null) {
            return;
        }

        if (channelUsers.remove(userId)) {
            var partedChannelUserDto = sharingSession.toChannelUserDto();
            channelUsers.stream()
                    .map(x -> userSessions.getOrDefault(x, null))
                    .filter(x -> x != null && x.getUserId() != userId)
                    .forEach(x -> x.sendMessage(ChannelUserPayloadDto.builder().type(PayloadType.JOIN_USER).user(partedChannelUserDto).build()));
            sharingSession.sendMessage(ChannelUserPayloadDto.builder().type(PayloadType.CHANNEL_PARTED).user(partedChannelUserDto).build());
        }
    }

    private void OnRelaySessionDescription(WebSocketSession session, UserAuthenticationDetails userAuthenticationDetails, RelaySessionDescriptionPayloadDto relaySessionDescriptionPayloadDto) {
        var userId = userAuthenticationDetails.getId();
        var channelId = userAuthenticationDetails.getChannelId();

        var channelUsers = channels.get(channelId);
        if (channelUsers == null || !channelUsers.contains(relaySessionDescriptionPayloadDto.getUserId())) {
            return;
        }

        userSessions.get(relaySessionDescriptionPayloadDto.getUserId())
                .sendMessage(RelaySessionDescriptionPayloadDto
                        .builder()
                        .type(PayloadType.RELAY_SESSION_DESCRIPTION)
                        .userId(userId)
                        .sessionDescription(relaySessionDescriptionPayloadDto.getSessionDescription())
                        .build());
    }

    private void OnRelayIceCandidate(WebSocketSession session, UserAuthenticationDetails userAuthenticationDetails, RelayIceCandidatePayloadDto relayIceCandidatePayloadDto) {
        var userId = userAuthenticationDetails.getId();
        var channelId = userAuthenticationDetails.getChannelId();

        var channelUsers = channels.get(channelId);
        if (channelUsers == null || !channelUsers.contains(relayIceCandidatePayloadDto.getUserId())) {
            return;
        }

        userSessions.get(relayIceCandidatePayloadDto.getUserId())
                .sendMessage(RelayIceCandidatePayloadDto
                        .builder()
                        .type(PayloadType.RELAY_ICE_CANDIDATE)
                        .userId(userId)
                        .iceCandidate(relayIceCandidatePayloadDto.getIceCandidate())
                        .build());
    }

    private void OnReaction(UserAuthenticationDetails userAuthenticationDetails, ReactionPayloadDto reactionPayloadDto) {
        var channelId = userAuthenticationDetails.getChannelId();
        var emoji = reactionPayloadDto.getEmoji();
        if (emoji == null || emoji.isBlank() || emoji.length() > 16) {
            return;
        }
        var payload = ReactionPayloadDto.builder()
                .type(PayloadType.REACTION)
                .emoji(emoji)
                .userId(userAuthenticationDetails.getId())
                .build();
        // Broadcast to everyone except the sender (the sender animates locally).
        broadcastToChannel(channelId, payload, userAuthenticationDetails.getId());
    }

    private void OnKick(UserAuthenticationDetails userAuthenticationDetails, KickPayloadDto kickPayloadDto) {
        if (!userAuthenticationDetails.isHost()) {
            return;
        }
        var channelId = userAuthenticationDetails.getChannelId();
        var targetUserId = kickPayloadDto.getUserId();
        if (targetUserId == null || targetUserId.equals(userAuthenticationDetails.getId())) {
            return;
        }
        var channelUsers = channels.get(channelId);
        if (channelUsers == null || !channelUsers.contains(targetUserId)) {
            return;
        }
        var targetSession = userSessions.get(targetUserId);
        if (targetSession == null) {
            return;
        }
        // Notify the target, then close their session. afterConnectionClosed
        // takes care of removing them and broadcasting PART_USER to the rest.
        targetSession.sendMessage(KickPayloadDto.builder().type(PayloadType.KICKED).userId(targetUserId).build());
        try {
            targetSession.getSession().close(CloseStatus.NORMAL);
        } catch (IOException ignored) {
        }
    }

    private void OnRequestPresent(UserAuthenticationDetails userAuthenticationDetails) {
        var channelId = userAuthenticationDetails.getChannelId();
        var hostUserId = findHostUserId(channelId);
        if (hostUserId == null) {
            return;
        }
        var hostSession = userSessions.get(hostUserId);
        if (hostSession == null) {
            return;
        }
        hostSession.sendMessage(PresenterPayloadDto.builder()
                .type(PayloadType.REQUEST_PRESENT)
                .userId(userAuthenticationDetails.getId())
                .build());
    }

    private void OnSetPresenter(UserAuthenticationDetails userAuthenticationDetails, PresenterPayloadDto presenterPayloadDto) {
        var channelId = userAuthenticationDetails.getChannelId();
        var channelUsers = channels.get(channelId);
        if (channelUsers == null) {
            return;
        }
        // Only the host, or the user currently presenting, may change the presenter.
        var currentPresenter = channelPresenters.get(channelId);
        var authorized = userAuthenticationDetails.isHost()
                || userAuthenticationDetails.getId().equals(currentPresenter);
        if (!authorized) {
            return;
        }
        // A null target means "release presenting back to the host".
        var targetUserId = presenterPayloadDto.getUserId();
        if (targetUserId == null) {
            targetUserId = findHostUserId(channelId);
        }
        if (targetUserId == null || !channelUsers.contains(targetUserId)) {
            return;
        }
        channelPresenters.put(channelId, targetUserId);
        broadcastToChannel(channelId, PresenterPayloadDto.builder()
                .type(PayloadType.PRESENTER_CHANGED)
                .userId(targetUserId)
                .build(), null);
    }

    private UUID findHostUserId(UUID channelId) {
        var channelUsers = channels.get(channelId);
        if (channelUsers == null) {
            return null;
        }
        return channelUsers.stream()
                .map(userSessions::get)
                .filter(Objects::nonNull)
                .filter(s -> s.getRoleType() == RoleType.HOST)
                .map(SharingSession::getUserId)
                .findFirst()
                .orElse(null);
    }

    private void broadcastToChannel(UUID channelId, PayloadDto payload, UUID excludeUserId) {
        var channelUsers = channels.get(channelId);
        if (channelUsers == null) {
            return;
        }
        channelUsers.stream()
                .map(userSessions::get)
                .filter(Objects::nonNull)
                .filter(s -> excludeUserId == null || !s.getUserId().equals(excludeUserId))
                .forEach(s -> s.sendMessage(payload));
    }

    public void broadcastNewMessage(UUID channelId, SimpleMessageDto simpleMessageDto) {
        var channelUsers = channels.get(channelId);
        if (channelUsers == null) {
            return;
        }

        var newMessagePayload = NewMessagePayloadDto.builder().type(PayloadType.NEW_MESSAGE).message(simpleMessageDto).build();
        channelUsers.stream()
                .map(x -> userSessions.get(x))
                .forEach(x -> x.sendMessage(newMessagePayload));
    }

    public void broadcastUserUpdated(UUID channelId, ChannelUserDto updatedUser) {
        var channelUsers = channels.get(channelId);
        if (channelUsers == null) {
            return;
        }

        var payload = ChannelUserPayloadDto.builder().type(PayloadType.USER_UPDATED).user(updatedUser).build();
        channelUsers.stream()
                .map(userSessions::get)
                .filter(Objects::nonNull)
                .forEach(session -> session.sendMessage(payload));
    }

    public CollectionDto<ChannelUserDto> getChannelUsers(UUID channelId) {
        var channelUsers = channels.get(channelId);
        if (channelUsers == null) {
            return CollectionDto.<ChannelUserDto>builder()
                    .data(Collections.emptyList())
                    .size(0)
                    .build();
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

    /**
     * Channel ids that currently have at least one connected user.
     */
    public Set<UUID> getActiveChannelIds() {
        return channels.entrySet().stream()
                .filter(entry -> entry.getValue() != null && !entry.getValue().isEmpty())
                .map(Map.Entry::getKey)
                .collect(Collectors.toSet());
    }

    /**
     * Channel ids that currently have a connected host (used for the public list).
     */
    public Set<UUID> getActiveChannelIdsWithHost() {
        return channels.entrySet().stream()
                .filter(entry -> entry.getValue() != null && entry.getValue().stream().anyMatch(userId -> {
                    var sharingSession = userSessions.get(userId);
                    return sharingSession != null && sharingSession.getRoleType() == RoleType.HOST;
                }))
                .map(Map.Entry::getKey)
                .collect(Collectors.toSet());
    }

    public int getChannelUserCount(UUID channelId) {
        var channelUsers = channels.get(channelId);
        return channelUsers == null ? 0 : channelUsers.size();
    }

    public Set<UUID> getChannelUserIds(UUID channelId) {
        var channelUsers = channels.get(channelId);
        return channelUsers == null ? Collections.emptySet() : new HashSet<>(channelUsers);
    }
}
