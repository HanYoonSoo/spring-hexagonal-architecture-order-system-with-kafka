package com.hanyoonsoo.ordersystem.adapter.out.redis.stock;

import com.hanyoonsoo.ordersystem.adapter.config.redis.CacheType;
import com.hanyoonsoo.ordersystem.adapter.config.redis.RedisCacheProperties;
import com.hanyoonsoo.ordersystem.adapter.out.persistence.jpa.product.repository.ProductJpaRepository;
import com.hanyoonsoo.ordersystem.application.order.port.out.StockReservationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
@RequiredArgsConstructor
public class RedisStockReservationAdapter implements StockReservationRepository {

    private static final String STOCK_KEY_PREFIX = "stock:product:";

    private final RedisTemplate<String, String> redisTemplate;
    private final ProductJpaRepository productJpaRepository;
    private final RedisCacheProperties redisCacheProperties;

    @Override
    public boolean reserve(Long productId, Long quantity) {
        Long currentStock = getCurrentStock(productId);
        if (currentStock == null || currentStock < quantity) {
            return false;
        }

        int decreased = productJpaRepository.updateProductStockByProductId(productId, quantity);
        if (decreased == 0) {
            return false;
        }

        long updatedStock = currentStock - quantity;
        if (updatedStock <= 0L) {
            redisTemplate.delete(stockKey(productId));
            return true;
        }
        redisTemplate.opsForValue().set(
                stockKey(productId),
                String.valueOf(updatedStock),
                cacheTtl()
        );
        return true;
    }

    private Long getCurrentStock(Long productId) {
        String key = stockKey(productId);
        String cachedStock = redisTemplate.opsForValue().get(key);
        if (cachedStock != null) {
            try {
                return Long.parseLong(cachedStock);
            } catch (NumberFormatException exception) {
                redisTemplate.delete(key);
            }
        }

        Long dbStock = productJpaRepository.findProductStockByProductId(productId).orElse(null);
        if (dbStock == null) {
            return null;
        }
        if (dbStock <= 0L) {
            redisTemplate.delete(key);
            return dbStock;
        }
        redisTemplate.opsForValue().set(key, String.valueOf(dbStock), cacheTtl());
        return dbStock;
    }

    private String stockKey(Long productId) {
        return STOCK_KEY_PREFIX + productId;
    }

    private Duration cacheTtl() {
        return redisCacheProperties.ttlFor(CacheType.STOCK);
    }
}
