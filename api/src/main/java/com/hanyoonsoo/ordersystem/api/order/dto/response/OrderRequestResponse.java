package com.hanyoonsoo.ordersystem.api.order.dto.response;

import com.hanyoonsoo.ordersystem.core.domain.order.entity.OrderStatus;

import java.util.UUID;

public record OrderRequestResponse(
        UUID orderId,
        String status
) {
    public static OrderRequestResponse from(UUID orderId, OrderStatus status) {
        return new OrderRequestResponse(orderId, status.name());
    }
}
