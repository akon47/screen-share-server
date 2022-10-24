package com.hwans.screenshareserver.common.security.jwt.stomp;


import com.hwans.screenshareserver.common.Constants;
import com.hwans.screenshareserver.common.errors.errorcode.ErrorCodes;
import com.hwans.screenshareserver.common.errors.exception.RestApiException;
import com.hwans.screenshareserver.common.security.jwt.JwtStatus;
import com.hwans.screenshareserver.common.security.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.naming.NameNotFoundException;
import java.util.regex.Pattern;

@Component
@RequiredArgsConstructor
@Order(Ordered.HIGHEST_PRECEDENCE + 99)
public class JwtStompAuthenticationHandler implements ChannelInterceptor {
    private final JwtTokenProvider tokenProvider;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
        var command = accessor.getCommand();

        if (StompCommand.CONNECT.equals(command) || StompCommand.SUBSCRIBE.equals(command)) {
            String bearerToken = accessor.getFirstNativeHeader(Constants.AUTHORIZATION_HEADER);
            String jwt = tokenProvider.extractTokenFromHeader(bearerToken);
            if(tokenProvider.validateToken(jwt) != JwtStatus.ACCESS) {
                throw new RuntimeException("invalid token");
            } else if(StompCommand.SUBSCRIBE.equals(command)) {
                var m = Pattern.compile("^/channels/(.*?)$").matcher(accessor.getDestination());
                if(m.find()) {
                    var subscribeChannel = m.group(1);
                    var tokenChannel = tokenProvider.getChannelIdFromToken(jwt).orElse("");
                    if(!subscribeChannel.equals(tokenChannel)) {
                        throw new RuntimeException("permission denied");
                    }
                } else {
                    throw new RuntimeException("channel not found");
                }
            }
        }
        return message;
    }
}
