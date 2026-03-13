package com.hanyoonsoo.ordersystem.api.auth.dto;

import jakarta.validation.constraints.NotBlank;

public record RefreshTokenReissueRequest(
        @NotBlank String refreshToken
) {
}
