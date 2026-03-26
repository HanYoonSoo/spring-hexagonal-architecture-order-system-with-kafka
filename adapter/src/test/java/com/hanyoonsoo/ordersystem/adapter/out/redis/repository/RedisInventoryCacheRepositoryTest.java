package com.hanyoonsoo.ordersystem.adapter.out.redis.repository;

import com.hanyoonsoo.ordersystem.adapter.support.container.IntegrationTestContainerSupporter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;

class RedisInventoryCacheRepositoryTest extends IntegrationTestContainerSupporter {

    @Autowired
    private RedisInventoryCacheRepository repository;

    @Test
    void 상품_ID로_재고를_조회하면_파싱된_재고값을_반환한다() {
        // given
        redisTemplate.opsForValue().set("inventory:product:1", "10");

        // when
        var actual = repository.findStockByProductId(1L);

        // then
        assertThat(actual).contains(10L);
    }

    @Test
    void 잘못된_재고_캐시값은_삭제한다() {
        // given
        redisTemplate.opsForValue().set("inventory:product:1", "invalid");

        // when
        var actual = repository.findStockByProductId(1L);

        // then
        assertThat(actual).isEmpty();
        assertThat(redisTemplate.hasKey("inventory:product:1")).isFalse();
    }

    @Test
    void 재고를_저장하면_설정된_TTL과_함께_저장한다() {
        // given

        // when
        repository.saveStock(1L, 5L);

        // then
        assertThat(redisTemplate.opsForValue().get("inventory:product:1")).isEqualTo("5");
        assertThat(redisTemplate.getExpire("inventory:product:1")).isNotNull().isPositive();
    }

    @Test
    void 재고를_삭제하면_키를_제거한다() {
        // given
        redisTemplate.opsForValue().set("inventory:product:1", "5");

        // when
        repository.removeStock(1L);

        // then
        assertThat(redisTemplate.hasKey("inventory:product:1")).isFalse();
    }
}
