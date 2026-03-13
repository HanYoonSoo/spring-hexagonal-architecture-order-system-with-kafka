package com.hanyoonsoo.ordersystem.api.auth.jwt;

import java.time.Duration;

public record TokenDto(
        String accessToken,
        String refreshToken,
        Duration refreshTokenExpiration
) {
}
