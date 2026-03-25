package com.hanyoonsoo.ordersystem.adapter.out.redis.support;

import java.util.UUID;

public final class RedisKeyFactory {

    private static final String DELIMITER = ":";
    private static final String INVENTORY_PRODUCT_PREFIX = "inventory:product:";
    private static final String USER_PREFIX = "user:";
    private static final String RECENTLY_VIEWED_PRODUCTS_SUFFIX = "recently-viewed-products";
    private static final String IDEMPOTENCY_KEY_PREFIX = "idempotency-key:";

    private RedisKeyFactory() {
    }

    public static String inventoryProduct(Long productId) {
        return INVENTORY_PRODUCT_PREFIX + productId;
    }

    public static String userRecentlyViewedProducts(UUID userId) {
        return USER_PREFIX + userId + DELIMITER + RECENTLY_VIEWED_PRODUCTS_SUFFIX;
    }

    public static String idempotencyKey(String idempotencyKey) {
        return IDEMPOTENCY_KEY_PREFIX + idempotencyKey;
    }
}
