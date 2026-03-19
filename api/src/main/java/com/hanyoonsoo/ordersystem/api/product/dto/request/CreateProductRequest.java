package com.hanyoonsoo.ordersystem.api.product.dto.request;

import com.hanyoonsoo.ordersystem.application.product.dto.CreateProductCommand;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record CreateProductRequest(
        @NotBlank String name,
        @NotBlank String description,
        @NotNull @Positive Long price,
        @NotNull @Positive Long stock
) {
    public CreateProductCommand toCommand() {
        return new CreateProductCommand(name, description, price, stock);
    }
}
