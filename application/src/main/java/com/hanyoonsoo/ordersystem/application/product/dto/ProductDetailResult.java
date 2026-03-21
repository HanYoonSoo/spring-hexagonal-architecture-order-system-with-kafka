package com.hanyoonsoo.ordersystem.application.product.dto;

import com.hanyoonsoo.ordersystem.core.domain.product.entity.Product;

public record ProductDetailResult(
        Long id,
        String name,
        String description,
        Long price,
        Long stock
) {
    public static ProductDetailResult from(Product product) {
        return new ProductDetailResult(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getPrice(),
                product.getStock()
        );
    }
}
