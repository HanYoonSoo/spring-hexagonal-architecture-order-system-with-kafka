package com.hanyoonsoo.ordersystem.application.product.port.in;

import com.hanyoonsoo.ordersystem.application.product.dto.CreateProductCommand;
import com.hanyoonsoo.ordersystem.application.product.dto.ProductDetailResult;

public interface ProductServicePort {

    ProductDetailResult getProductInfo(Long productId);

    void createProduct(CreateProductCommand command);
}
