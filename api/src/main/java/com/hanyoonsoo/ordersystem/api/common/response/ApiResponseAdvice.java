package com.hanyoonsoo.ordersystem.api.common.response;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hanyoonsoo.ordersystem.common.exception.ErrorResponse;
import com.hanyoonsoo.ordersystem.common.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.core.MethodParameter;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.converter.ByteArrayHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

@RestControllerAdvice(basePackages = "com.hanyoonsoo.ordersystem.api")
@RequiredArgsConstructor
public class ApiResponseAdvice implements ResponseBodyAdvice<Object> {

    private final ObjectMapper objectMapper;

    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        return true;
    }

    @Override
    public Object beforeBodyWrite(
            Object body,
            MethodParameter returnType,
            MediaType selectedContentType,
            Class<? extends HttpMessageConverter<?>> selectedConverterType,
            ServerHttpRequest request,
            ServerHttpResponse response
    ) {
        if (shouldSkipWrap(body, selectedConverterType, request)) {
            return body;
        }

        if (body instanceof ErrorResponse errorResponse) {
            return ApiResponse.failure(errorResponse);
        }

        ApiResponse<Object> wrapped = ApiResponse.success(body);

        if (StringHttpMessageConverter.class.isAssignableFrom(selectedConverterType)) {
            try {
                return objectMapper.writeValueAsString(wrapped);
            } catch (JsonProcessingException e) {
                throw new IllegalStateException("Failed to serialize ApiResponse", e);
            }
        }
        return wrapped;
    }

    private boolean shouldSkipWrap(
            Object body,
            Class<? extends HttpMessageConverter<?>> selectedConverterType,
            ServerHttpRequest request
    ) {
        if (body instanceof ApiResponse<?>) {
            return true;
        }
        if (body instanceof Resource || body instanceof byte[] || body instanceof StreamingResponseBody) {
            return true;
        }
        if (ByteArrayHttpMessageConverter.class.isAssignableFrom(selectedConverterType)) {
            return true;
        }
        String path = extractPath(request);
        return path.startsWith("/actuator")
                || path.startsWith("/v3/api-docs")
                || path.startsWith("/swagger-ui");
    }

    private String extractPath(ServerHttpRequest request) {
        if (request instanceof ServletServerHttpRequest servletRequest) {
            return servletRequest.getServletRequest().getRequestURI();
        }
        return request.getURI().getPath();
    }
}
