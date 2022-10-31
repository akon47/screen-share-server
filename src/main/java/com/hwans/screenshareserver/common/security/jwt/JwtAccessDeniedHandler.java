package com.hwans.screenshareserver.common.security.jwt;

import com.hwans.screenshareserver.common.errors.errorcode.ErrorCodes;
import com.hwans.screenshareserver.common.errors.exception.RestApiException;
import com.hwans.screenshareserver.dto.common.ErrorResponseDto;
import nonapi.io.github.classgraph.json.JSONSerializer;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class JwtAccessDeniedHandler implements AccessDeniedHandler {
    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException {
        // 필요한 권한이 없이 접근하려 할때 403
        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);

        response.getWriter().print(JSONSerializer.serializeObject(new ErrorResponseDto(new RestApiException(ErrorCodes.Forbidden.FORBIDDEN))));
    }
}
