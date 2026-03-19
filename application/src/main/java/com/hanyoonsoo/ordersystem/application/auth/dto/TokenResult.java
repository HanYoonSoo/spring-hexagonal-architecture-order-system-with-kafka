package com.hanyoonsoo.ordersystem.application.auth.dto;

import java.time.Duration;

public record TokenResult(
        String accessToken,
        String refreshToken,
        Duration refreshTokenExpiration
) {
}
