package com.hanyoonsoo.ordersystem.api.order.dto.request;

import com.hanyoonsoo.ordersystem.application.order.dto.CreateOrderCommand;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.util.UUID;

public record CreateOrderRequest(
        @NotNull Long productId,
        @NotNull @Positive Long quantity
) {

    public CreateOrderCommand toCommand(UUID userId) {
        return new CreateOrderCommand(userId, productId, quantity);
    }
}
