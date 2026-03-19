package com.hanyoonsoo.ordersystem.application.product.dto;

public record ProductDetailResult(
        Long id,
        String name,
        String description,
        Long price,
        Long stock
) {
}
