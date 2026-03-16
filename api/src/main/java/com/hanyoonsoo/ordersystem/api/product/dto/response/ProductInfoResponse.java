package com.hanyoonsoo.ordersystem.api.product.dto.response;

import com.hanyoonsoo.ordersystem.application.product.dto.ProductInfoDto;

public record ProductInfoResponse(
        Long productId,
        String name,
        String description,
        Long price,
        Long stock
) {
    public static ProductInfoResponse from(ProductInfoDto dto) {
        return new ProductInfoResponse(
                dto.id(),
                dto.name(),
                dto.description(),
                dto.price(),
                dto.stock()
        );
    }
}
