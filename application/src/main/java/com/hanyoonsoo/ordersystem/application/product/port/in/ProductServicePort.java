package com.hanyoonsoo.ordersystem.application.product.port.in;

import com.hanyoonsoo.ordersystem.application.product.dto.ProductCreateCommand;
import com.hanyoonsoo.ordersystem.application.product.dto.ProductInfoDto;

public interface ProductServicePort {

    ProductInfoDto getProductInfo(Long productId);

    void createProduct(ProductCreateCommand command);
}
