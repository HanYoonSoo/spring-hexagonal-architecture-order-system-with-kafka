package com.hanyoonsoo.ordersystem.core.support.fixture;

import com.hanyoonsoo.ordersystem.core.domain.product.entity.Product;

public final class ProductFixture {

    private ProductFixture() {
    }

    public static Product 상품() {
        return Product.of("keyboard", "mechanical", 10000L, 5L);
    }
}
