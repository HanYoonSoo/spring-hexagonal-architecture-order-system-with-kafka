package com.hanyoonsoo.ordersystem.adapter.out.redis.repository;

import com.hanyoonsoo.ordersystem.adapter.support.container.IntegrationTestContainerSupporter;
import com.hanyoonsoo.ordersystem.application.idempotency.model.IdempotencyKeyMetadata;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class RedisIdempotencyKeyRepositoryTest extends IntegrationTestContainerSupporter {

    @Autowired
    private RedisIdempotencyKeyRepository repository;

    @Test
    void 멱등성_키를_saveIfAbsent로_저장하며_메타데이터를_JSON으로_남긴다() {
        // given
        IdempotencyKeyMetadata metadata = new IdempotencyKeyMetadata("POST", "/api/v1/orders", LocalDateTime.of(2026, 3, 26, 12, 0));

        // when
        boolean firstSaved = repository.saveIfAbsentIdempotencyKey("idem-key", metadata);
        boolean secondSaved = repository.saveIfAbsentIdempotencyKey("idem-key", metadata);
        String actualValue = redisTemplate.opsForValue().get("idempotency-key:idem-key");

        // then
        assertThat(firstSaved).isTrue();
        assertThat(secondSaved).isFalse();
        assertThat(actualValue).contains("\"method\":\"POST\"");
        assertThat(actualValue).contains("\"path\":\"/api/v1/orders\"");
    }
}
