package com.hanyoonsoo.ordersystem.api.product.controller;

import com.hanyoonsoo.ordersystem.api.product.dto.request.CreateProductRequest;
import com.hanyoonsoo.ordersystem.api.product.dto.response.ProductDetailResponse;
import com.hanyoonsoo.ordersystem.application.product.port.in.ProductServicePort;
import com.hanyoonsoo.ordersystem.common.response.ApiResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductServicePort productService;

    @GetMapping("/{productId}")
    public ApiResponse<ProductDetailResponse> getProductInfo(
            @PathVariable @Positive Long productId
    ) {
        ProductDetailResponse response = ProductDetailResponse.from(productService.getProductInfo(productId));
        return ApiResponse.success(response);
    }

    @PostMapping("")
    public ApiResponse<Void> createProduct(
            @Valid @RequestBody CreateProductRequest request
    ) {
        productService.createProduct(request.toCommand());
        return ApiResponse.success(null);
    }
}
