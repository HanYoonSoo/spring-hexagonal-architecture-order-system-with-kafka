package com.hanyoonsoo.ordersystem.adapter.out.redis.repository;

import com.hanyoonsoo.ordersystem.application.auth.port.out.AuthRedisRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;

@Repository
@RequiredArgsConstructor
public class AuthRedisRepositoryAdapter implements AuthRedisRepository {

    private static final String LOGOUT_VALUE = "logout";

    private final RedisTemplate<String, String> redisTemplate;

    @Override
    public void saveRefreshToken(String key, String refreshToken, long expireMillis) {
        redisTemplate.opsForValue().set(key, refreshToken, Duration.ofMillis(expireMillis));
    }

    @Override
    public String findRefreshToken(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    @Override
    public void deleteRefreshToken(String key) {
        redisTemplate.delete(key);
    }

    @Override
    public void saveAccessTokenForLogout(String accessToken, long expireMillis) {
        redisTemplate.opsForValue().set(accessToken, LOGOUT_VALUE, Duration.ofMillis(expireMillis));
    }

    @Override
    public boolean isLogoutAccessToken(String accessToken) {
        return LOGOUT_VALUE.equals(redisTemplate.opsForValue().get(accessToken));
    }
}
