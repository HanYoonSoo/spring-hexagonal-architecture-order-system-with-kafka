package com.hanyoonsoo.ordersystem.api.auth.controller;

import com.hanyoonsoo.ordersystem.api.auth.config.CorsAllowedOriginsProperties;
import com.hanyoonsoo.ordersystem.api.auth.config.CookieProperties;
import com.hanyoonsoo.ordersystem.api.auth.dto.SignInRequest;
import com.hanyoonsoo.ordersystem.api.auth.utils.AuthCookieUtils;
import com.hanyoonsoo.ordersystem.application.auth.dto.TokenResult;
import com.hanyoonsoo.ordersystem.application.auth.port.in.AuthServicePort;
import com.hanyoonsoo.ordersystem.common.exception.ErrorCode;
import com.hanyoonsoo.ordersystem.common.exception.base.UnauthorizedException;
import com.hanyoonsoo.ordersystem.common.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.Arrays;
import java.util.Objects;

@RestController
@RequestMapping("/api/v1/auth")
@Tag(name = "Auth", description = "인증 API")
@RequiredArgsConstructor
public class AuthController {

    private static final String BEARER_PREFIX = "Bearer ";

    private final AuthServicePort authService;
    private final CorsAllowedOriginsProperties corsAllowedOriginsProperties;
    private final CookieProperties cookieProperties;
    private final Environment environment;

    @Operation(summary = "로그인")
    @PostMapping("/sign-in")
    public ApiResponse<Void> signIn(
            @Valid @RequestBody SignInRequest request,
            HttpServletResponse response
    ) {
        TokenResult tokenResponse = authService.signIn(request.toCommand());
        setTokenToResponse(response, tokenResponse);
        return ApiResponse.success(null);
    }

    @Operation(summary = "토큰 재갱신")
    @PostMapping("/reissue")
    public ApiResponse<Void> reissue(
            @Parameter(description = "Access Token (Authorization: Bearer ...)")
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authorization,
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        throwIfNotAllowedOrigin(request);
        String refreshToken = AuthCookieUtils.extractRefreshToken(request.getCookies());
        TokenResult tokenResponse = authService.reissue(extractAccessToken(authorization), refreshToken);
        setTokenToResponse(response, tokenResponse);
        return ApiResponse.success(null);
    }

    private String extractAccessToken(String authorization) {
        if (authorization == null || !authorization.startsWith(BEARER_PREFIX)) {
            throw new UnauthorizedException(ErrorCode.INVALID_TOKEN);
        }
        return authorization.substring(BEARER_PREFIX.length());
    }

    private void setTokenToResponse(HttpServletResponse response, TokenResult tokenResponse) {
        response.addHeader(HttpHeaders.AUTHORIZATION, BEARER_PREFIX + tokenResponse.accessToken());
        response.addHeader(HttpHeaders.SET_COOKIE, AuthCookieUtils.buildRefreshTokenCookie(tokenResponse, cookieProperties).toString());
    }

    private void throwIfNotAllowedOrigin(HttpServletRequest request) {
        if (!isProdProfile()) {
            return;
        }

        String origin = request.getHeader("Origin");
        if (origin == null || origin.isBlank()) {
            String referer = request.getHeader("Referer");
            if (referer != null && !referer.isBlank()) {
                try {
                    URI uri = URI.create(referer);
                    origin = uri.getScheme() + "://" + uri.getHost();
                } catch (IllegalArgumentException ignored) {
                    origin = null;
                }
            }
        }

        if (origin == null) {
            throw new UnauthorizedException(ErrorCode.UNAUTHORIZED_ORIGIN);
        }

        String finalOrigin = origin;
        boolean allowed = corsAllowedOriginsProperties.getOrigins().stream()
                .filter(Objects::nonNull)
                .anyMatch(allowedOrigin -> allowedOrigin.equals(finalOrigin));

        if (!allowed) {
            throw new UnauthorizedException(ErrorCode.UNAUTHORIZED_ORIGIN);
        }
    }

    private boolean isProdProfile() {
        return Arrays.asList(environment.getActiveProfiles()).contains("prod");
    }
}
