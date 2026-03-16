package com.hanyoonsoo.ordersystem.application.product.dto;

public record ProductInfoDto(
        Long id,
        String name,
        String description,
        Long price,
        Long stock
) {
}
