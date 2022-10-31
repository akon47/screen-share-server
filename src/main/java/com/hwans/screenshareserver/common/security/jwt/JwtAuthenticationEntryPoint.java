package com.hwans.screenshareserver.common.security.jwt;

import com.hwans.screenshareserver.common.errors.errorcode.ErrorCodes;
import com.hwans.screenshareserver.common.errors.exception.RestApiException;
import com.hwans.screenshareserver.dto.common.ErrorResponseDto;
import nonapi.io.github.classgraph.json.JSONSerializer;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        var restApiException = new RestApiException(ErrorCodes.Unauthorized.UNAUTHORIZED);
        var exception = request.getAttribute("exception");
        if(RestApiException.class.isInstance(exception)) {
            restApiException = RestApiException.class.cast(exception);
        }

        response.getWriter().print(JSONSerializer.serializeObject(new ErrorResponseDto(restApiException)));
    }
}
