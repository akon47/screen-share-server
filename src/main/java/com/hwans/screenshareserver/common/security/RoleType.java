package com.hwans.screenshareserver.common.security;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Optional;
import java.util.stream.Stream;

@Getter
@RequiredArgsConstructor
public enum RoleType {
    UNKNOWN("ROLE_UNKNOWN", "알 수 없는 권한"),
    GUEST("ROLE_GUEST", "게스트 권한"),
    HOST("ROLE_HOST", "호스트 권한");

    private final String name;
    private final String displayName;

    public static RoleType fromName(String name) {
        return Stream.of(RoleType.values()).filter(x -> x.getName().equals(name)).findFirst().orElseThrow(() -> new IllegalArgumentException());
    }
}