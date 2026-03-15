package com.hanyoonsoo.ordersystem.api.auth.utils;

import com.hanyoonsoo.ordersystem.api.auth.config.CookieProperties;
import com.hanyoonsoo.ordersystem.application.auth.dto.TokenDto;
import com.hanyoonsoo.ordersystem.application.auth.port.in.AuthServicePort;
import com.hanyoonsoo.ordersystem.common.exception.ErrorCode;
import com.hanyoonsoo.ordersystem.common.exception.base.UnauthorizedException;
import jakarta.servlet.http.Cookie;
import org.springframework.http.ResponseCookie;

import java.time.Duration;

public final class AuthCookieUtils {

    private AuthCookieUtils() {
    }

    public static ResponseCookie buildRefreshTokenCookie(TokenDto tokenDto, CookieProperties cookieProperties) {
        return ResponseCookie.from(AuthServicePort.REFRESH_TOKEN_COOKIE_NAME, tokenDto.refreshToken())
                .httpOnly(true)
                .secure(cookieProperties.isSecure())
                .path("/")
                .sameSite(cookieProperties.getSameSite())
                .maxAge(Duration.ofMillis(tokenDto.refreshTokenExpiration().toMillis()))
                .build();
    }

    public static String extractRefreshToken(Cookie[] cookies) {
        if (cookies == null || cookies.length == 0) {
            throw new UnauthorizedException(ErrorCode.REFRESH_TOKEN_EXPIRED);
        }

        for (Cookie cookie : cookies) {
            if (AuthServicePort.REFRESH_TOKEN_COOKIE_NAME.equals(cookie.getName())) {
                return cookie.getValue();
            }
        }

        throw new UnauthorizedException(ErrorCode.REFRESH_TOKEN_EXPIRED);
    }
}
