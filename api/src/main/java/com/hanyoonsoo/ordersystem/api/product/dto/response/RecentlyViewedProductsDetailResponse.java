package com.hanyoonsoo.ordersystem.api.product.dto.response;

import com.hanyoonsoo.ordersystem.application.product.dto.ProductDetailResult;

import java.util.List;

public record RecentlyViewedProductsDetailResponse(
        List<ProductDetailResult> recentlyViewedProducts
) {
}
