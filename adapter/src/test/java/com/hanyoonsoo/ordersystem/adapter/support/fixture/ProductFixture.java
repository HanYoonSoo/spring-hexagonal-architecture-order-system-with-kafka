package com.hanyoonsoo.ordersystem.adapter.support.fixture;

import com.hanyoonsoo.ordersystem.core.domain.product.entity.Product;

import java.lang.reflect.Field;

public final class ProductFixture {

    private ProductFixture() {
    }

    public static Product 상품(Long id) {
        Product product = Product.of("keyboard", "mechanical", 10000L, 5L);
        setField(product, "id", id);
        return product;
    }

    private static void setField(Object target, String fieldName, Object value) {
        try {
            Field field = target.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(target, value);
        } catch (ReflectiveOperationException exception) {
            throw new IllegalStateException(exception);
        }
    }
}
