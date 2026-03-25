package com.hanyoonsoo.ordersystem.application.idempotency.model;

import java.time.LocalDateTime;

public record IdempotencyKeyMetadata(
        String method,
        String path,
        LocalDateTime createdAt
) {
}
