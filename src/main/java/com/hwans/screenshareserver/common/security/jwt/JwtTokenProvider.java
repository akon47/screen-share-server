package com.hwans.screenshareserver.common.security.jwt;

import com.hwans.screenshareserver.common.Constants;
import com.hwans.screenshareserver.common.security.RoleType;
import com.hwans.screenshareserver.service.authentication.UserAuthenticationDetails;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.security.Key;
import java.util.*;
import java.util.stream.Collectors;

@Component
@Slf4j
public class JwtTokenProvider implements InitializingBean {
    private static final String ROLE_TYPE_KEY = "role-type";
    private static final String CHANNEL_ID_KEY = "channel-id";
    private final String tokenSecretKeyBase64Secret;

    private Key tokenSecretKey;

    public JwtTokenProvider(@Value("${jwt.base64-secret}") String accessTokenSecretKeyBase64Secret) {
        this.tokenSecretKeyBase64Secret = accessTokenSecretKeyBase64Secret;
        log.debug("tokenSecretKeyBase64Secret -> " + accessTokenSecretKeyBase64Secret);
    }

    @Override
    public void afterPropertiesSet() {
        byte[] accessSecretKeyBytes = Decoders.BASE64.decode(tokenSecretKeyBase64Secret);
        this.tokenSecretKey = Keys.hmacShaKeyFor(accessSecretKeyBytes);
    }

    public String extractTokenFromHeader(String token) {
        if (StringUtils.hasText(token) && token.startsWith("Bearer ")) {
            return token.substring(7);
        }
        return token;
    }

    public JwtStatus validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(tokenSecretKey).build().parseClaimsJws(token);
            return JwtStatus.ACCESS;
        } catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException e) {
            log.info("Invalid JWT signature.");
            log.trace("Invalid JWT signature trace: {}", e);
        } catch (ExpiredJwtException e) {
            log.info("Expired JWT token.");
            log.trace("Expired JWT token trace: {}", e);
            return JwtStatus.EXPIRED;
        } catch (UnsupportedJwtException e) {
            log.info("Unsupported JWT token.");
            log.trace("Unsupported JWT token trace: {}", e);
        } catch (IllegalArgumentException e) {
            log.info("JWT token compact of handler are invalid.");
            log.trace("JWT token compact of handler are invalid trace: {}", e);
        }
        return JwtStatus.DENIED;
    }

    public UserAuthenticationDetails getUserAuthenticationDetails(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(tokenSecretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();

        Collection<? extends GrantedAuthority> authorities =
                Arrays.stream(claims.get(ROLE_TYPE_KEY).toString().split(","))
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList());

        return new UserAuthenticationDetails(UUID.fromString(claims.getSubject()), UUID.fromString(claims.get(CHANNEL_ID_KEY).toString()), authorities);
    }

    public Authentication getAuthentication(String token) {
        var principal = getUserAuthenticationDetails(token);
        return new UsernamePasswordAuthenticationToken(principal, token, principal.getAuthorities());
    }

    public String createHostToken(UUID userId, UUID channelId) {
        long now = (new Date()).getTime();
        Date registerTokenExpiresIn = new Date(now + Constants.HOST_TOKEN_EXPIRES_TIME);
        String registerToken = Jwts.builder()
                .setSubject(userId.toString())
                .claim(ROLE_TYPE_KEY, RoleType.HOST.getName())
                .claim(CHANNEL_ID_KEY, channelId.toString())
                .signWith(tokenSecretKey, SignatureAlgorithm.HS256)
                .setExpiration(registerTokenExpiresIn)
                .compact();
        return registerToken;
    }

    public String createGuestToken(UUID userId, UUID channelId) {
        long now = (new Date()).getTime();
        Date registerTokenExpiresIn = new Date(now + Constants.GUEST_TOKEN_EXPIRES_TIME);
        String registerToken = Jwts.builder()
                .setSubject(userId.toString())
                .claim(ROLE_TYPE_KEY, RoleType.GUEST.getName())
                .claim(CHANNEL_ID_KEY, channelId.toString())
                .signWith(tokenSecretKey, SignatureAlgorithm.HS256)
                .setExpiration(registerTokenExpiresIn)
                .compact();
        return registerToken;
    }
}
