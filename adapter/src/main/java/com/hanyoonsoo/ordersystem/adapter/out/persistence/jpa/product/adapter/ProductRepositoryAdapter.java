package com.hanyoonsoo.ordersystem.adapter.out.persistence.jpa.product.adapter;

import com.hanyoonsoo.ordersystem.adapter.out.persistence.jpa.product.repository.ProductJpaRepository;
import com.hanyoonsoo.ordersystem.application.product.port.out.ProductRepository;
import com.hanyoonsoo.ordersystem.core.domain.product.entity.Product;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class ProductRepositoryAdapter implements ProductRepository {

    private final ProductJpaRepository productJpaRepository;

    @Override
    public boolean existsById(Long productId) {
        return productJpaRepository.existsProductById(productId);
    }

    @Override
    public Optional<Product> findProductById(Long productId) {
        return productJpaRepository.findProductById(productId);
    }
}
