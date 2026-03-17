package com.hanyoonsoo.ordersystem.application.order.event;

import java.time.OffsetDateTime;
import java.util.UUID;

public record OrderCreatedEvent(
        UUID eventId,
        String eventType,
        OffsetDateTime occurredAt,
        UUID orderId,
        UUID userId,
        Long productId,
        Long quantity
) {}
