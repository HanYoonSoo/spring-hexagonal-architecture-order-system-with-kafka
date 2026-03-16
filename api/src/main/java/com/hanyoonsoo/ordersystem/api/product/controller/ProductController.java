package com.hanyoonsoo.ordersystem.api.product.controller;

import com.hanyoonsoo.ordersystem.api.product.dto.response.ProductInfoResponse;
import com.hanyoonsoo.ordersystem.application.product.port.in.ProductServicePort;
import com.hanyoonsoo.ordersystem.common.response.ApiResponse;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductServicePort productService;

    @GetMapping("/{productId}")
    public ApiResponse<ProductInfoResponse> getProductInfo(
            @PathVariable @Positive Long productId
    ) {
        ProductInfoResponse response = ProductInfoResponse.from(productService.getProductInfo(productId));
        return ApiResponse.success(response);
    }
}
