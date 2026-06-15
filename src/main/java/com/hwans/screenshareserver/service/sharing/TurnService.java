package com.hwans.screenshareserver.service.sharing;

import com.hwans.screenshareserver.dto.sharing.TurnCredentialsDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

/**
 * Generates short-lived ICE servers (STUN/TURN) using the Cloudflare Realtime
 * TURN service. When no Cloudflare credentials are configured, an empty list is
 * returned so the client falls back to its STUN-only configuration.
 */
@Slf4j
@Service
public class TurnService {
    // The credential lifetime; should comfortably exceed a sharing session.
    private static final int CREDENTIAL_TTL_SECONDS = 86400;

    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${cloudflare.turn.key-id:}")
    private String turnKeyId;

    @Value("${cloudflare.turn.api-token:}")
    private String turnApiToken;

    public TurnCredentialsDto generateCredentials() {
        if (!StringUtils.hasText(turnKeyId) || !StringUtils.hasText(turnApiToken)) {
            return TurnCredentialsDto.builder().iceServers(List.of()).build();
        }

        var url = "https://rtc.live.cloudflare.com/v1/turn/keys/" + turnKeyId + "/credentials/generate-ice-servers";

        var headers = new HttpHeaders();
        headers.setBearerAuth(turnApiToken);
        headers.setContentType(MediaType.APPLICATION_JSON);
        var requestEntity = new HttpEntity<>(Map.of("ttl", CREDENTIAL_TTL_SECONDS), headers);

        try {
            var response = restTemplate.postForObject(url, requestEntity, TurnCredentialsDto.class);
            if (response != null && response.getIceServers() != null) {
                return response;
            }
        } catch (Exception e) {
            log.warn("Failed to generate Cloudflare TURN credentials: {}", e.getMessage());
        }
        return TurnCredentialsDto.builder().iceServers(List.of()).build();
    }
}
