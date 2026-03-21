package com.hanyoonsoo.ordersystem.application.product.port.out;

import com.hanyoonsoo.ordersystem.application.product.dto.ProductDetailResult;
import com.hanyoonsoo.ordersystem.core.domain.product.entity.Product;

import java.util.List;
import java.util.Optional;

public interface ProductRepository {

    boolean existsById(Long productId);

    Optional<Product> findProductById(Long productId);

    List<Product> findProductsByIds(List<Long> productIds);

    void save(Product entity);
}
