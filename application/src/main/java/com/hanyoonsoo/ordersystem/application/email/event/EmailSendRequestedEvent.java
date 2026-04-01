package com.hanyoonsoo.ordersystem.application.email.event;

import com.hanyoonsoo.ordersystem.core.domain.order.entity.OrderStatus;

import java.time.LocalDateTime;
import java.util.UUID;

public record EmailSendRequestedEvent(
        UUID eventId,
        String eventType,
        LocalDateTime occurredAt,
        UUID orderId,
        UUID userId,
        String email,
        Long productId,
        Long quantity,
        OrderStatus orderStatus
) {
}
