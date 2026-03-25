package com.hanyoonsoo.ordersystem.api.auth.custom;

import com.hanyoonsoo.ordersystem.api.common.response.ApiFilterErrorResponseWriter;
import com.hanyoonsoo.ordersystem.common.exception.ErrorCode;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    private final ApiFilterErrorResponseWriter apiFilterErrorResponseWriter;

    @Override
    public void handle(
            HttpServletRequest request,
            HttpServletResponse response,
            AccessDeniedException accessDeniedException
    ) throws IOException {
        String message = accessDeniedException != null && accessDeniedException.getMessage() != null
                ? accessDeniedException.getMessage()
                : "Forbidden";

        apiFilterErrorResponseWriter.write(
                request,
                response,
                ErrorCode.FORBIDDEN_USER,
                message,
                request.getRequestURI()
        );
    }
}
