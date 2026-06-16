package com.hwans.screenshareserver.dto.sharing;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PayloadType {
    JOIN_CHANNEL,
    PART_CHANNEL,
    RELAY_SESSION_DESCRIPTION,
    RELAY_ICE_CANDIDATE,

    JOIN_USER,
    PART_USER,
    USER_UPDATED,
    NEW_MESSAGE,
    CHANNEL_JOINED,
    CHANNEL_PARTED,

    // Emoji reaction broadcast.
    REACTION,
    // Host kicks a user (KICK request) / target is notified (KICKED).
    KICK,
    KICKED,

    // Host pen-drawing annotation broadcast over the shared screen.
    DRAW,
    // Host clears all drawing annotations.
    CLEAR_DRAW,
}
