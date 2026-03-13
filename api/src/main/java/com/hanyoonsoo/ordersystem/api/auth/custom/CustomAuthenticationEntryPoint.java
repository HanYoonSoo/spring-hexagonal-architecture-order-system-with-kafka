package com.hanyoonsoo.ordersystem.api.auth.custom;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hanyoonsoo.ordersystem.api.auth.config.CorsAllowedOriginsProperties;
import com.hanyoonsoo.ordersystem.api.auth.exception.JwtAuthenticationException;
import com.hanyoonsoo.ordersystem.common.exception.ErrorCode;
import com.hanyoonsoo.ordersystem.common.exception.ErrorResponse;
import com.hanyoonsoo.ordersystem.common.response.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper;
    private final CorsAllowedOriginsProperties corsAllowedOriginsProperties;

    @Override
    public void commence(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException authException
    ) throws IOException {
        response.setCharacterEncoding("UTF-8");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        CustomCorsHeaderConfigurer.setCorsHeader(request, response, corsAllowedOriginsProperties.getOrigins());

        String code = ErrorCode.AUTHENTICATION_FAILED.getCode();
        String message = ErrorCode.AUTHENTICATION_FAILED.getMessage();
        if (authException instanceof JwtAuthenticationException jwtAuthenticationException) {
            code = jwtAuthenticationException.getErrorCode().getCode();
            message = jwtAuthenticationException.getMessage();
        } else if (authException != null && authException.getMessage() != null) {
            message = authException.getMessage();
        }

        ErrorResponse errorResponse = ErrorResponse.of(code, message, request.getRequestURI());
        objectMapper.writeValue(response.getWriter(), ApiResponse.failure(errorResponse));
    }
}
