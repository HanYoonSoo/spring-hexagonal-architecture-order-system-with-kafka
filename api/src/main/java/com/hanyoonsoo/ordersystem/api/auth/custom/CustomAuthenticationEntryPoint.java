package com.hanyoonsoo.ordersystem.api.auth.custom;

import com.hanyoonsoo.ordersystem.api.auth.exception.JwtAuthenticationException;
import com.hanyoonsoo.ordersystem.api.common.response.ApiFilterErrorResponseWriter;
import com.hanyoonsoo.ordersystem.common.exception.ErrorCode;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ApiFilterErrorResponseWriter apiFilterErrorResponseWriter;

    @Override
    public void commence(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException authException
    ) throws IOException {
        ErrorCode errorCode = ErrorCode.AUTHENTICATION_FAILED;
        String message = ErrorCode.AUTHENTICATION_FAILED.getMessage();
        if (authException instanceof JwtAuthenticationException jwtAuthenticationException) {
            errorCode = jwtAuthenticationException.getErrorCode();
            message = jwtAuthenticationException.getMessage();
        } else if (authException != null && authException.getMessage() != null) {
            message = authException.getMessage();
        }

        apiFilterErrorResponseWriter.write(
                request,
                response,
                errorCode,
                message,
                request.getRequestURI()
        );
    }
}
