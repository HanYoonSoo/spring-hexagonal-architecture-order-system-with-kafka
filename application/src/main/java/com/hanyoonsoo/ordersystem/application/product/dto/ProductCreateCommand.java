package com.hanyoonsoo.ordersystem.application.product.dto;

import com.hanyoonsoo.ordersystem.core.domain.product.entity.Product;

public record ProductCreateCommand(
        String name,
        String description,
        Long price,
        Long stock
) {
    public Product toEntity() {
        return Product.of(
                name,
                description,
                price,
                stock
        );
    }
}
