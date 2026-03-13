package com.hanyoonsoo.ordersystem.api.common.exception;

import com.hanyoonsoo.ordersystem.api.auth.exception.JwtAuthenticationException;
import com.hanyoonsoo.ordersystem.common.exception.ErrorCode;
import com.hanyoonsoo.ordersystem.common.exception.ErrorResponse;
import com.hanyoonsoo.ordersystem.common.exception.base.GlobalException;
import com.hanyoonsoo.ordersystem.common.response.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.ConstraintViolationException;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(basePackages = "com.hanyoonsoo.ordersystem.api")
public class GlobalExceptionHandler {

    @ExceptionHandler(GlobalException.class)
    public ApiResponse<Void> handleGlobalException(
            GlobalException exception,
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        return buildResponse(
                response,
                exception.status(),
                exception.code(),
                exception.getMessage(),
                request.getRequestURI()
        );
    }

    @ExceptionHandler(JwtAuthenticationException.class)
    public ApiResponse<Void> handleJwtAuthenticationException(
            JwtAuthenticationException exception,
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        return buildResponse(
                response,
                exception.getErrorCode().getStatus(),
                exception.getErrorCode().getCode(),
                exception.getMessage(),
                request.getRequestURI()
        );
    }

    @ExceptionHandler({MethodArgumentNotValidException.class, BindException.class, ConstraintViolationException.class})
    public ApiResponse<Void> handleValidationException(
            Exception exception,
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        String message = resolveValidationMessage(exception);
        return buildResponse(response, ErrorCode.INVALID_REQUEST, message, request.getRequestURI());
    }

    @ExceptionHandler(Exception.class)
    public ApiResponse<Void> handleException(
            Exception exception,
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        return buildResponse(
                response,
                ErrorCode.INTERNAL_SERVER_ERROR,
                ErrorCode.INTERNAL_SERVER_ERROR.getMessage(),
                request.getRequestURI()
        );
    }

    private ApiResponse<Void> buildResponse(
            HttpServletResponse response,
            int status,
            String code,
            String message,
            String path
    ) {
        response.setStatus(status);
        ErrorResponse errorResponse = ErrorResponse.of(code, message, path);
        return ApiResponse.failure(errorResponse);
    }

    private ApiResponse<Void> buildResponse(
            HttpServletResponse response,
            ErrorCode errorCode,
            String message,
            String path
    ) {
        return buildResponse(response, errorCode.getStatus(), errorCode.getCode(), message, path);
    }

    private String resolveValidationMessage(Exception exception) {
        if (exception instanceof MethodArgumentNotValidException methodArgumentNotValidException) {
            return methodArgumentNotValidException.getBindingResult().getFieldErrors().stream()
                    .findFirst()
                    .map(error -> error.getDefaultMessage() == null ? "Invalid request" : error.getDefaultMessage())
                    .orElse("Invalid request");
        }
        if (exception instanceof BindException bindException) {
            return bindException.getBindingResult().getFieldErrors().stream()
                    .findFirst()
                    .map(error -> error.getDefaultMessage() == null ? "Invalid request" : error.getDefaultMessage())
                    .orElse("Invalid request");
        }
        if (exception instanceof ConstraintViolationException constraintViolationException) {
            return constraintViolationException.getConstraintViolations().stream()
                    .findFirst()
                    .map(violation -> violation.getMessage() == null ? "Invalid request" : violation.getMessage())
                    .orElse("Invalid request");
        }
        return "Invalid request";
    }
}
