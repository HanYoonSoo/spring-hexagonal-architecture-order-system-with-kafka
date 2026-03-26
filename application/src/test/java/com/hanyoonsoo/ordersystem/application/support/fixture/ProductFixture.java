package com.hanyoonsoo.ordersystem.application.support.fixture;

import com.hanyoonsoo.ordersystem.application.product.dto.CreateProductCommand;
import com.hanyoonsoo.ordersystem.core.domain.product.entity.Product;

import java.lang.reflect.Field;
import java.util.List;

public final class ProductFixture {

    private ProductFixture() {
    }

    public static CreateProductCommand 상품생성명령() {
        return new CreateProductCommand("keyboard", "mechanical", 10000L, 5L);
    }

    public static Product 상품(Long id) {
        Product product = Product.of("keyboard", "mechanical", 10000L, 5L);
        setField(product, "id", id);
        return product;
    }

    public static List<Product> 상품목록(Long... ids) {
        return java.util.Arrays.stream(ids).map(ProductFixture::상품).toList();
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
