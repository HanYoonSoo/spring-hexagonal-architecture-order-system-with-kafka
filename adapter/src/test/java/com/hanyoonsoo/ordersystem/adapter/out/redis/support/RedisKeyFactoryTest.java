package com.hanyoonsoo.ordersystem.adapter.out.redis.support;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class RedisKeyFactoryTest {

    @Test
    void 재고_상품_키를_생성한다() {
        // given
        Long productId = 1L;

        // when
        String key = RedisKeyFactory.inventoryProduct(productId);

        // then
        assertThat(key).isEqualTo("inventory:product:1");
    }

    @Test
    void 사용자_최근_본_상품_키를_계층형으로_생성한다() {
        // given
        UUID userId = UUID.fromString("11111111-1111-1111-1111-111111111111");

        // when
        String key = RedisKeyFactory.userRecentlyViewedProducts(userId);

        // then
        assertThat(key).isEqualTo("user:11111111-1111-1111-1111-111111111111:recently-viewed-products");
    }

    @Test
    void 멱등성_키를_생성한다() {
        // given
        String idempotencyKey = "idem-key";

        // when
        String key = RedisKeyFactory.idempotencyKey(idempotencyKey);

        // then
        assertThat(key).isEqualTo("idempotency-key:idem-key");
    }
}
