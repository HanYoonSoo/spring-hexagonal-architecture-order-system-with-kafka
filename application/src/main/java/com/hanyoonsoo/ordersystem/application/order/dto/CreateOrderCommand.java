package com.hanyoonsoo.ordersystem.application.order.dto;

import java.util.UUID;

public record CreateOrderCommand(
        UUID userId,
        Long productId,
        Long quantity
) {
}
