package com.hwans.screenshareserver.common.security;

import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.UUID;

@Getter
public class UserAuthenticationDetails extends User implements UserDetails {
    private final UUID id;
    private final UUID channelId;

    public UserAuthenticationDetails(UUID id, UUID channelId, Collection<? extends GrantedAuthority> authorities) {
        super(id.toString(), id.toString(), authorities);

        this.id = id;
        this.channelId = channelId;
    }

    public boolean isHost() {
        return getAuthorities().stream().anyMatch(authority -> authority.getAuthority().equals(RoleType.HOST.toString()));
    }

    public boolean isGuest() {
        return getAuthorities().stream().anyMatch(authority -> authority.getAuthority().equals(RoleType.GUEST.toString()));
    }
}
