package com.hanyoonsoo.ordersystem.api.common.response;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hanyoonsoo.ordersystem.api.auth.config.CorsAllowedOriginsProperties;
import com.hanyoonsoo.ordersystem.api.auth.custom.CustomCorsHeaderConfigurer;
import com.hanyoonsoo.ordersystem.common.exception.ErrorCode;
import com.hanyoonsoo.ordersystem.common.exception.ErrorResponse;
import com.hanyoonsoo.ordersystem.common.response.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Component
@RequiredArgsConstructor
public class ApiFilterErrorResponseWriter {

    private final ObjectMapper objectMapper;
    private final CorsAllowedOriginsProperties corsAllowedOriginsProperties;

    public void write(
            HttpServletRequest request,
            HttpServletResponse response,
            ErrorCode errorCode,
            String message,
            String path
    ) throws IOException {
        CustomCorsHeaderConfigurer.setCorsHeader(request, response, corsAllowedOriginsProperties.getOrigins());
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setStatus(errorCode.getStatus());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        ErrorResponse errorResponse = ErrorResponse.of(errorCode.getCode(), message, path);
        objectMapper.writeValue(response.getWriter(), ApiResponse.failure(errorResponse));
    }
}
