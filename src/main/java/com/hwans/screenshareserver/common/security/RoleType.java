package com.hwans.screenshareserver.common.security;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum RoleType {
    GUEST("ROLE_GUEST", "게스트 권한"),
    HOST("ROLE_HOST", "호스트 권한");

    private final String name;
    private final String displayName;
}