package com.hanyoonsoo.ordersystem.adapter.out.redis.repository;

import com.hanyoonsoo.ordersystem.adapter.support.container.IntegrationTestContainerSupporter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class RedisRecentlyViewedProductRepositoryTest extends IntegrationTestContainerSupporter {

    @Autowired
    private RedisRecentlyViewedProductRepository repository;

    @Test
    void 최근_본_상품을_추가하면_중복을_제거하고_최신_다섯개만_유지한다() {
        // given
        UUID userId = UUID.fromString("11111111-1111-1111-1111-111111111111");

        // when
        repository.addRecentlyViewedProduct(userId, 1L);
        repository.addRecentlyViewedProduct(userId, 2L);
        repository.addRecentlyViewedProduct(userId, 3L);
        repository.addRecentlyViewedProduct(userId, 4L);
        repository.addRecentlyViewedProduct(userId, 5L);
        repository.addRecentlyViewedProduct(userId, 3L);
        repository.addRecentlyViewedProduct(userId, 6L);
        List<Long> actual = repository.getRecentlyViewedProducts(userId);

        // then
        assertThat(actual).containsExactly(6L, 3L, 5L, 4L, 2L);
        assertThat(redisTemplate.getExpire("user:11111111-1111-1111-1111-111111111111:recently-viewed-products"))
                .isNotNull()
                .isPositive();
    }

    @Test
    void Redis에_값이_없으면_최근_본_상품_목록은_빈_리스트를_반환한다() {
        // given
        UUID userId = UUID.randomUUID();

        // when
        List<Long> actual = repository.getRecentlyViewedProducts(userId);

        // then
        assertThat(actual).isEmpty();
    }

    @Test
    void 최근_본_상품_목록은_문자열_ID를_Long으로_변환한다() {
        // given
        UUID userId = UUID.randomUUID();
        redisTemplate.opsForList().rightPushAll("user:" + userId + ":recently-viewed-products", List.of("3", "2", "1"));

        // when
        List<Long> actual = repository.getRecentlyViewedProducts(userId);

        // then
        assertThat(actual).containsExactly(3L, 2L, 1L);
    }
}
