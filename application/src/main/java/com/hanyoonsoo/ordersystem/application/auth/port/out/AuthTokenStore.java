package com.hanyoonsoo.ordersystem.application.auth.port.out;

public interface AuthTokenStore {

    void saveRefreshToken(String key, String refreshToken, long expireMillis);

    String findRefreshToken(String key);

    void deleteRefreshToken(String key);

    void saveAccessTokenForLogout(String accessToken, long expireMillis);

    boolean isLogoutAccessToken(String accessToken);
}
