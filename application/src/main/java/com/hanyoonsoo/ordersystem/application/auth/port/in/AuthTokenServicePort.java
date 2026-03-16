package com.hanyoonsoo.ordersystem.application.auth.port.in;

public interface AuthTokenServicePort {

    void saveRefreshToken(String userId, String refreshToken, long expireMillis);

    void matchRefreshTokenOrThrow(String userId, String refreshToken);

    void deleteRefreshToken(String userId);

    void saveAccessTokenForLogout(String accessToken, long expireMillis);

    boolean isLogoutAccessToken(String accessToken);
}
