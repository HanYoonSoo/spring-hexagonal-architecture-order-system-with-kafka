package com.hanyoonsoo.ordersystem.adapter.out.persistence.jpa.product.adapter;

import com.hanyoonsoo.ordersystem.adapter.out.persistence.jpa.product.repository.ProductJpaRepository;
import com.hanyoonsoo.ordersystem.application.product.port.out.ProductRepository;
import com.hanyoonsoo.ordersystem.application.product.port.out.ProductStockRepository;
import com.hanyoonsoo.ordersystem.core.domain.product.entity.Product;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

@Repository
@RequiredArgsConstructor
public class ProductRepositoryAdapter implements ProductRepository, ProductStockRepository {

    private final ProductJpaRepository productJpaRepository;

    @Override
    public boolean existsById(Long productId) {
        return productJpaRepository.existsProductById(productId);
    }

    @Override
    public Optional<Product> findProductById(Long productId) {
        return productJpaRepository.findProductById(productId);
    }

    @Override
    public List<Product> findProductsByIds(List<Long> productIds) {
        Map<Long, Product> productById = productJpaRepository.findAllById(productIds).stream()
                .collect(java.util.stream.Collectors.toMap(Product::getId, Function.identity()));

        return productIds.stream()
                .map(productById::get)
                .filter(java.util.Objects::nonNull)
                .toList();
    }

    @Override
    public void save(Product entity) { productJpaRepository.save(entity); }

    @Override
    public Optional<Long> findStockByProductId(Long productId) {
        return productJpaRepository.findProductStockByProductId(productId);
    }

    @Override
    public boolean decreaseStock(Long productId, Long quantity) {
        return productJpaRepository.updateProductStockByProductId(productId, quantity) > 0;
    }
}
