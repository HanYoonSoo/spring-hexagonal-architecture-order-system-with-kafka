package com.hanyoonsoo.ordersystem.application.order.event;

import com.hanyoonsoo.ordersystem.core.domain.order.entity.OrderStatus;

import java.time.LocalDateTime;
import java.util.UUID;

public record OrderResultEvent(
        UUID eventId,
        String eventType,
        LocalDateTime occurredAt,
        UUID orderId,
        UUID userId,
        Long productId,
        Long quantity,
        OrderStatus orderStatus
) {
}
