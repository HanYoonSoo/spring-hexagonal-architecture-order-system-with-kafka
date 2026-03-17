package com.hanyoonsoo.ordersystem.api.product.dto.request;

import com.hanyoonsoo.ordersystem.application.product.dto.ProductCreateCommand;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record ProductCreateRequest(
        @NotBlank String name,
        @NotBlank String description,
        @NotNull @Positive Long price,
        @NotNull @Positive Long stock
) {
    public ProductCreateCommand toCommand() {
        return new ProductCreateCommand(name, description, price, stock);
    }
}
