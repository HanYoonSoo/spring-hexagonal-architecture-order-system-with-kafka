package com.hanyoonsoo.ordersystem.adapter.config.redis;

import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class RedisCachePropertiesTest {

    @Test
    void 캐시별_TTL이_설정되어_있으면_그_값을_사용한다() {
        // given
        RedisCacheProperties properties = new RedisCacheProperties();
        properties.setDefaultTtlSeconds(60);
        properties.setTtlSeconds(Map.of("stock", 120L));

        // when
        Duration ttl = properties.ttlFor(CacheType.STOCK);

        // then
        assertThat(ttl).isEqualTo(Duration.ofSeconds(120));
    }

    @Test
    void 캐시별_TTL이_없으면_기본값을_사용하고_최소_1초를_보장한다() {
        // given
        RedisCacheProperties properties = new RedisCacheProperties();
        properties.setDefaultTtlSeconds(0);

        // when
        Duration ttl = properties.ttlFor(CacheType.RECENTLY_VIEWED_PRODUCT);

        // then
        assertThat(ttl).isEqualTo(Duration.ofSeconds(1));
    }
}
