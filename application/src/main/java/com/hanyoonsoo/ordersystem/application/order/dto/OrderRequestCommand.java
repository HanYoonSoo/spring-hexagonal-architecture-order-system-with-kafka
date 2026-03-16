package com.hanyoonsoo.ordersystem.application.order.dto;

import java.util.UUID;

public record OrderRequestCommand(
        UUID userId,
        Long productId,
        Long quantity
) {
}
