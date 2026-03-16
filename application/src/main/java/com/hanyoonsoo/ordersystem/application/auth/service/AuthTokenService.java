package com.hanyoonsoo.ordersystem.application.auth.service;

import com.hanyoonsoo.ordersystem.application.auth.port.in.AuthTokenServicePort;
import com.hanyoonsoo.ordersystem.application.auth.port.out.AuthTokenStore;
import com.hanyoonsoo.ordersystem.common.exception.ErrorCode;
import com.hanyoonsoo.ordersystem.common.exception.base.UnauthorizedException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthTokenService implements AuthTokenServicePort {

    private static final String USER_KEY_PREFIX = "user:";
    private static final String REFRESH_TOKEN_KEY_SUFFIX = ":refreshToken";

    private final AuthTokenStore authTokenStore;

    @Override
    public void saveRefreshToken(String userId, String refreshToken, long expireMillis) {
        authTokenStore.saveRefreshToken(makeRefreshTokenKey(userId), refreshToken, expireMillis);
    }

    @Override
    public void matchRefreshTokenOrThrow(String userId, String refreshToken) {
        String refreshTokenInRedis = authTokenStore.findRefreshToken(makeRefreshTokenKey(userId));
        if (refreshTokenInRedis == null || refreshTokenInRedis.isBlank()) {
            throw new UnauthorizedException(ErrorCode.REFRESH_TOKEN_EXPIRED);
        }
        if (!refreshTokenInRedis.equals(refreshToken)) {
            throw new UnauthorizedException(ErrorCode.REFRESH_TOKEN_MISMATCH);
        }
    }

    @Override
    public void deleteRefreshToken(String userId) {
        authTokenStore.deleteRefreshToken(makeRefreshTokenKey(userId));
    }

    @Override
    public void saveAccessTokenForLogout(String accessToken, long expireMillis) {
        authTokenStore.saveAccessTokenForLogout(accessToken, expireMillis);
    }

    @Override
    public boolean isLogoutAccessToken(String accessToken) {
        return authTokenStore.isLogoutAccessToken(accessToken);
    }

    private String makeRefreshTokenKey(String userId) {
        return USER_KEY_PREFIX + userId + REFRESH_TOKEN_KEY_SUFFIX;
    }
}
