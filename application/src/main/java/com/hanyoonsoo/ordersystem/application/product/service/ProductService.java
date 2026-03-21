package com.hanyoonsoo.ordersystem.application.product.service;

import com.hanyoonsoo.ordersystem.application.common.transaction.ReadOnlyTransactional;
import com.hanyoonsoo.ordersystem.application.product.dto.CreateProductCommand;
import com.hanyoonsoo.ordersystem.application.product.dto.ProductDetailResult;
import com.hanyoonsoo.ordersystem.application.product.port.in.ProductServicePort;
import com.hanyoonsoo.ordersystem.application.product.port.out.ProductRepository;
import com.hanyoonsoo.ordersystem.application.product.port.out.RecentlyViewedProductRepository;
import com.hanyoonsoo.ordersystem.common.exception.ErrorCode;
import com.hanyoonsoo.ordersystem.common.exception.base.NotFoundException;
import com.hanyoonsoo.ordersystem.core.domain.product.entity.Product;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductService implements ProductServicePort {

    private final ProductRepository productRepository;
    private final RecentlyViewedProductRepository recentlyViewedProductRepository;

    @Override
    @ReadOnlyTransactional
    public ProductDetailResult getProductInfo(Long productId) {
        Product product = productRepository.findProductById(productId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.PRODUCT_NOT_FOUND));

        return ProductDetailResult.from(product);
    }

    @Override
    public void createProduct(CreateProductCommand command) {
        productRepository.save(command.toEntity());
    }

    @Override
    public void addRecentlyViewedProduct(UUID userId, Long productId) {
        recentlyViewedProductRepository.addRecentlyViewedProduct(userId, productId);
    }

    @Override
    @ReadOnlyTransactional
    public List<ProductDetailResult> getRecentlyViewedProducts(UUID userId) {
        List<Long> productIds = recentlyViewedProductRepository.getRecentlyViewedProducts(userId);

        return productRepository.findProductsByIds(productIds).stream().map(ProductDetailResult::from).toList();
    }
}
