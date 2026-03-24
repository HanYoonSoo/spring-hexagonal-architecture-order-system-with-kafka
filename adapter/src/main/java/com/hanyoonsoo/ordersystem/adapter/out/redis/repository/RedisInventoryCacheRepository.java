package com.hanyoonsoo.ordersystem.adapter.out.redis.repository;

import com.hanyoonsoo.ordersystem.adapter.config.redis.CacheType;
import com.hanyoonsoo.ordersystem.adapter.config.redis.RedisCacheProperties;
import com.hanyoonsoo.ordersystem.application.product.port.out.InventoryCacheRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class RedisInventoryCacheRepository implements InventoryCacheRepository {

    private static final String INVENTORY_KEY_PREFIX = "inventory:product:";

    private final RedisTemplate<String, String> redisTemplate;
    private final RedisCacheProperties redisCacheProperties;

    @Override
    public Optional<Long> findStockByProductId(Long productId) {
        String key = stockKey(productId);
        String cachedStock = redisTemplate.opsForValue().get(key);
        if (cachedStock == null) {
            return Optional.empty();
        }
        try {
            return Optional.of(Long.parseLong(cachedStock));
        } catch (NumberFormatException exception) {
            redisTemplate.delete(key);
            return Optional.empty();
        }
    }

    @Override
    public void saveStock(Long productId, Long stock) {
        redisTemplate.opsForValue().set(stockKey(productId), String.valueOf(stock), cacheTtl());
    }

    @Override
    public void removeStock(Long productId) {
        redisTemplate.delete(stockKey(productId));
    }

    private String stockKey(Long productId) {
        return INVENTORY_KEY_PREFIX + productId;
    }

    private Duration cacheTtl() {
        return redisCacheProperties.ttlFor(CacheType.STOCK);
    }
}
