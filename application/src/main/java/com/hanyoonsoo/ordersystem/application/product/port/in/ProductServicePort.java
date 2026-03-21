package com.hanyoonsoo.ordersystem.application.product.port.in;

import com.hanyoonsoo.ordersystem.application.product.dto.CreateProductCommand;
import com.hanyoonsoo.ordersystem.application.product.dto.ProductDetailResult;

import java.util.List;
import java.util.UUID;

public interface ProductServicePort {

    ProductDetailResult getProductInfo(Long productId);

    void createProduct(CreateProductCommand command);

    void addRecentlyViewedProduct(UUID userId, Long productId);

    List<ProductDetailResult> getRecentlyViewedProducts(UUID userId);
}
