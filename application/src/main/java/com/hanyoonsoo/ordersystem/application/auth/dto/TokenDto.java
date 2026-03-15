package com.hanyoonsoo.ordersystem.application.auth.dto;

import java.time.Duration;

public record TokenDto(
        String accessToken,
        String refreshToken,
        Duration refreshTokenExpiration
) {
}
