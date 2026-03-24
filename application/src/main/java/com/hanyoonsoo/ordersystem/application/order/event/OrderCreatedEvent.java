package com.hanyoonsoo.ordersystem.application.order.event;

import java.time.LocalDateTime;
import java.util.UUID;

public record OrderCreatedEvent(
        UUID eventId,
        String eventType,
        LocalDateTime occurredAt,
        UUID orderId,
        UUID userId,
        Long productId,
        Long quantity
) {}
