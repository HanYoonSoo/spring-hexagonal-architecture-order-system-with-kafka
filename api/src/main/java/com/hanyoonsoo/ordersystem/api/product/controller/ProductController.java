package com.hanyoonsoo.ordersystem.api.product.controller;

import com.hanyoonsoo.ordersystem.api.auth.utils.SecurityUtils;
import com.hanyoonsoo.ordersystem.api.product.dto.request.CreateProductRequest;
import com.hanyoonsoo.ordersystem.api.product.dto.response.ProductDetailResponse;
import com.hanyoonsoo.ordersystem.api.product.dto.response.RecentlyViewedProductsDetailResponse;
import com.hanyoonsoo.ordersystem.application.product.dto.ProductDetailResult;
import com.hanyoonsoo.ordersystem.application.product.port.in.ProductServicePort;
import com.hanyoonsoo.ordersystem.common.response.ApiResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @PostMapping("/{productId}/recently-viewed")
    public ApiResponse<Void> recentlyViewProduct(
            @Positive @PathVariable("productId") Long productId
    ) {
        productService.addRecentlyViewedProduct(SecurityUtils.requiredAuthenticatedUserId(), productId);
        return ApiResponse.success(null);
    }

    @GetMapping("/recently-viewed")
    public ApiResponse<RecentlyViewedProductsDetailResponse> getRecentlyViewedProducts() {
        List<ProductDetailResult> products
                = productService.getRecentlyViewedProducts(SecurityUtils.requiredAuthenticatedUserId());
        return ApiResponse.success(new RecentlyViewedProductsDetailResponse(products));
    }
}
