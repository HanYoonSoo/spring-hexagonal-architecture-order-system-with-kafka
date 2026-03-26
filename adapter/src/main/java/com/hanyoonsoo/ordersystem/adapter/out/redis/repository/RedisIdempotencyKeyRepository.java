package com.hanyoonsoo.ordersystem.adapter.out.redis.repository;

import com.hanyoonsoo.ordersystem.adapter.out.redis.support.RedisKeyFactory;
import com.hanyoonsoo.ordersystem.application.idempotency.model.IdempotencyKeyMetadata;
import com.hanyoonsoo.ordersystem.application.idempotency.port.out.IdempotencyKeyRepository;
import com.hanyoonsoo.ordersystem.common.utils.ObjectMapperUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;

@Repository
@RequiredArgsConstructor
public class RedisIdempotencyKeyRepository implements IdempotencyKeyRepository {

    private final RedisTemplate<String, String> redisTemplate;
    private final ObjectMapperUtils objectMapperUtils;

    @Override
    public boolean saveIfAbsentIdempotencyKey(String idempotencyKey, IdempotencyKeyMetadata metadata) {
        Boolean saved = redisTemplate.opsForValue()
                .setIfAbsent(
                        RedisKeyFactory.idempotencyKey(idempotencyKey),
                        objectMapperUtils.writeValueAsString(metadata),
                        Duration.ofHours(1)
                );
        return Boolean.TRUE.equals(saved);
    }
}
