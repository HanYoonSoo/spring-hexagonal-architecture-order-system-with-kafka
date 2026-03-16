package com.hanyoonsoo.ordersystem.api.order.dto.response;

import java.util.UUID;

public record OrderRequestResponse(
        UUID orderId,
        String status
) {

    public static OrderRequestResponse pending(UUID orderId) {
        return new OrderRequestResponse(orderId, "PENDING");
    }
}
