package com.hanyoonsoo.ordersystem.api.idempotency.filter;

import com.hanyoonsoo.ordersystem.api.auth.config.AllowedPaths;
import com.hanyoonsoo.ordersystem.api.common.response.ApiFilterErrorResponseWriter;
import com.hanyoonsoo.ordersystem.application.idempotency.model.IdempotencyKeyMetadata;
import com.hanyoonsoo.ordersystem.application.idempotency.port.in.IdemPotencyServicePort;
import com.hanyoonsoo.ordersystem.common.exception.ErrorCode;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Arrays;

@Component
@RequiredArgsConstructor
public class IdempotencyKeyFilter extends OncePerRequestFilter {

    private static final String IDEMPOTENCY_KEY_HEADER = "X-Idempotency-Key";
    private static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();
    private final IdemPotencyServicePort idemPotencyServicePort;
    private final ApiFilterErrorResponseWriter apiFilterErrorResponseWriter;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }

        String idemPotencyKey = request.getHeader(IDEMPOTENCY_KEY_HEADER);

        if (!StringUtils.hasText(idemPotencyKey)) return true;

        String requestPath = request.getServletPath();
        return Arrays.stream(AllowedPaths.allowedPaths())
                .anyMatch(path -> PATH_MATCHER.match(path, requestPath));
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        String idemPotencyKey = request.getHeader(IDEMPOTENCY_KEY_HEADER);
        IdempotencyKeyMetadata metadata = new IdempotencyKeyMetadata(
                request.getMethod(),
                request.getRequestURI(),
                LocalDateTime.now()
        );

        boolean saved = idemPotencyServicePort.saveIfAbsentIdempotencyKey(idemPotencyKey, metadata);

        if (!saved) {
            apiFilterErrorResponseWriter.write(
                    request,
                    response,
                    ErrorCode.INVALID_REQUEST,
                    "이미 처리 중이거나 처리된 멱등 요청입니다.",
                    request.getRequestURI()
            );
            return;
        }

        filterChain.doFilter(request, response);
    }
}
