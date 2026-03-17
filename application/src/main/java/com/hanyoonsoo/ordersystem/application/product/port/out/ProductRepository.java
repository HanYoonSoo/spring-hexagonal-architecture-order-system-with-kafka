package com.hanyoonsoo.ordersystem.application.product.port.out;

import com.hanyoonsoo.ordersystem.core.domain.product.entity.Product;

import java.util.Optional;

public interface ProductRepository {

    boolean existsById(Long productId);

    Optional<Product> findProductById(Long productId);

    void save(Product entity);
}
