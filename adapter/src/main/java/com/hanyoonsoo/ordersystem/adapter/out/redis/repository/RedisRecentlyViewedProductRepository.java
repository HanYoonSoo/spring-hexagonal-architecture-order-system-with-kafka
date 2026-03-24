package com.hanyoonsoo.ordersystem.adapter.out.redis.repository;

import com.hanyoonsoo.ordersystem.adapter.config.redis.CacheType;
import com.hanyoonsoo.ordersystem.adapter.config.redis.RedisCacheProperties;
import com.hanyoonsoo.ordersystem.application.product.port.out.RecentlyViewedProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.util.List;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class RedisRecentlyViewedProductRepository implements RecentlyViewedProductRepository {

    private static final String USER_RECENTLY_VIEWED_PRODUCT_KEY_PREFIX = "user:recent-products:";

    private final RedisTemplate<String, String> redisTemplate;
    private final RedisCacheProperties redisCacheProperties;

    @Override
    public void addRecentlyViewedProduct(UUID userId, Long productId) {
        String key = USER_RECENTLY_VIEWED_PRODUCT_KEY_PREFIX + userId;

        redisTemplate.opsForList().remove(key, 0, String.valueOf(productId));
        redisTemplate.opsForList().leftPush(key, String.valueOf(productId));
        redisTemplate.opsForList().trim(key, 0, 4);
        redisTemplate.expire(key, cacheTtl());
    }

    @Override
    public List<Long> getRecentlyViewedProducts(UUID userId) {
        String key = USER_RECENTLY_VIEWED_PRODUCT_KEY_PREFIX + userId;
        List<String> productIds = redisTemplate.opsForList().range(key, 0, -1);
        
        if (productIds == null) return List.of();
        
        return productIds.stream()
                .map(Long::valueOf)
                .toList();
    }

    private Duration cacheTtl() {
        return redisCacheProperties.ttlFor(CacheType.RECENTLY_VIEWED_PRODUCT);
    }
}
