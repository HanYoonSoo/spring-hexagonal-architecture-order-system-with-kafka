package com.hanyoonsoo.ordersystem.application.product.port.out;

import java.util.List;
import java.util.UUID;

public interface RecentlyViewedProductRepository {
    void addRecentlyViewedProduct(UUID userId, Long productId);
    List<Long> getRecentlyViewedProducts(UUID userId);
}
