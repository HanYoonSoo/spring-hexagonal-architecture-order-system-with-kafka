package com.hanyoonsoo.ordersystem.api.order.dto.request;

import com.hanyoonsoo.ordersystem.application.order.dto.OrderRequestCommand;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.util.UUID;

public record OrderRequest(
        @NotNull Long productId,
        @NotNull @Positive Long quantity
) {

    public OrderRequestCommand toCommand(UUID userId) {
        return new OrderRequestCommand(userId, productId, quantity);
    }
}
