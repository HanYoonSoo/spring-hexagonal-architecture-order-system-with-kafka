package com.hanyoonsoo.ordersystem.api.order.dto.response;

import com.hanyoonsoo.ordersystem.core.domain.order.entity.OrderStatus;

import java.util.UUID;

public record CreateOrderResponse(
        UUID orderId,
        String status
) {
    public static CreateOrderResponse from(UUID orderId, OrderStatus status) {
        return new CreateOrderResponse(orderId, status.name());
    }
}
