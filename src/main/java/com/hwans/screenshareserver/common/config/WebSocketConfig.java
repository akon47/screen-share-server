package com.hwans.screenshareserver.common.config;

import com.hwans.screenshareserver.service.sharing.SharingService;
import com.hwans.screenshareserver.service.sharing.SharingWebSocketHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@RequiredArgsConstructor
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {
    private final SharingWebSocketHandler sharingWebSocketHandler;

    @Value("${allowedOrigins}")
    private String[] allowedOrigins;

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(sharingWebSocketHandler, "/ws")
                .setAllowedOrigins(allowedOrigins)
                .withSockJS();
    }
}
