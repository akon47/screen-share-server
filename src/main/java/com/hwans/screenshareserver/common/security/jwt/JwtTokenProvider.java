package com.hwans.screenshareserver.common.security.jwt;

import com.hwans.screenshareserver.common.Constants;
import com.hwans.screenshareserver.common.security.RoleType;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.security.Key;
import java.util.Date;
import java.util.UUID;

@Component
@Slf4j
public class JwtTokenProvider implements InitializingBean {
    private static final String AUTHORITIES_KEY = "auth";
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

    public String createHostToken(UUID channelId) {
        long now = (new Date()).getTime();
        Date registerTokenExpiresIn = new Date(now + Constants.HOST_TOKEN_EXPIRES_TIME);
        String registerToken = Jwts.builder()
                .setSubject(channelId.toString())
                .claim(AUTHORITIES_KEY, RoleType.HOST.getName())
                .signWith(tokenSecretKey, SignatureAlgorithm.HS256)
                .setExpiration(registerTokenExpiresIn)
                .compact();
        return registerToken;
    }

    public String createGuestToken(UUID channelId) {
        long now = (new Date()).getTime();
        Date registerTokenExpiresIn = new Date(now + Constants.GUEST_TOKEN_EXPIRES_TIME);
        String registerToken = Jwts.builder()
                .setSubject(channelId.toString())
                .claim(AUTHORITIES_KEY, RoleType.GUEST.getName())
                .signWith(tokenSecretKey, SignatureAlgorithm.HS256)
                .setExpiration(registerTokenExpiresIn)
                .compact();
        return registerToken;
    }
}
