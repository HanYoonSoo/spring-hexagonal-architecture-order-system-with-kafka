package com.hanyoonsoo.ordersystem.api.auth.custom;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hanyoonsoo.ordersystem.api.auth.config.CorsAllowedOriginsProperties;
import com.hanyoonsoo.ordersystem.common.exception.ErrorCode;
import com.hanyoonsoo.ordersystem.common.exception.ErrorResponse;
import com.hanyoonsoo.ordersystem.common.response.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    private final ObjectMapper objectMapper;
    private final CorsAllowedOriginsProperties corsAllowedOriginsProperties;

    @Override
    public void handle(
            HttpServletRequest request,
            HttpServletResponse response,
            AccessDeniedException accessDeniedException
    ) throws IOException {
        response.setCharacterEncoding("UTF-8");
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        CustomCorsHeaderConfigurer.setCorsHeader(request, response, corsAllowedOriginsProperties.getOrigins());

        String message = accessDeniedException != null && accessDeniedException.getMessage() != null
                ? accessDeniedException.getMessage()
                : "Forbidden";

        ErrorResponse errorResponse = ErrorResponse.of(ErrorCode.FORBIDDEN_USER.getCode(), message, request.getRequestURI());
        objectMapper.writeValue(response.getWriter(), ApiResponse.failure(errorResponse));
    }
}
