package com.hanyoonsoo.ordersystem.application.product.service;

import com.hanyoonsoo.ordersystem.application.common.transaction.ReadOnlyTransactional;
import com.hanyoonsoo.ordersystem.application.product.dto.ProductInfoDto;
import com.hanyoonsoo.ordersystem.application.product.port.in.ProductServicePort;
import com.hanyoonsoo.ordersystem.application.product.port.out.ProductRepository;
import com.hanyoonsoo.ordersystem.common.exception.ErrorCode;
import com.hanyoonsoo.ordersystem.common.exception.base.NotFoundException;
import com.hanyoonsoo.ordersystem.core.domain.product.entity.Product;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductService implements ProductServicePort {

    private final ProductRepository productRepository;

    @Override
    @ReadOnlyTransactional
    public ProductInfoDto getProductInfo(Long productId) {
        Product product = productRepository.findProductById(productId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.PRODUCT_NOT_FOUND));

        return new ProductInfoDto(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getPrice(),
                product.getStock()
        );
    }
}
