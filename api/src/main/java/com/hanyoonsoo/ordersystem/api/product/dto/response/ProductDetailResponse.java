package com.hanyoonsoo.ordersystem.api.product.dto.response;

import com.hanyoonsoo.ordersystem.application.product.dto.ProductDetailResult;

public record ProductDetailResponse(
        Long productId,
        String name,
        String description,
        Long price,
        Long stock
) {
    public static ProductDetailResponse from(ProductDetailResult dto) {
        return new ProductDetailResponse(
                dto.id(),
                dto.name(),
                dto.description(),
                dto.price(),
                dto.stock()
        );
    }
}
