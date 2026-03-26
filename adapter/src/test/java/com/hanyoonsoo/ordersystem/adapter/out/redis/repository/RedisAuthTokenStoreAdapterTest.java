package com.hanyoonsoo.ordersystem.adapter.out.redis.repository;

import com.hanyoonsoo.ordersystem.adapter.support.container.IntegrationTestContainerSupporter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;

class RedisAuthTokenStoreAdapterTest extends IntegrationTestContainerSupporter {

    @Autowired
    private RedisAuthTokenStoreAdapter adapter;

    @Test
    void 리프레시_토큰을_TTL과_함께_저장한다() {
        // given

        // when
        adapter.saveRefreshToken("user:1:refreshToken", "refresh-token", 1000L);
        Long ttl = redisTemplate.getExpire("user:1:refreshToken");

        // then
        assertThat(redisTemplate.opsForValue().get("user:1:refreshToken")).isEqualTo("refresh-token");
        assertThat(ttl).isNotNull().isBetween(0L, Duration.ofSeconds(1).toSeconds());
    }

    @Test
    void 저장된_리프레시_토큰을_조회한다() {
        // given
        redisTemplate.opsForValue().set("user:1:refreshToken", "refresh-token");

        // when
        String actual = adapter.findRefreshToken("user:1:refreshToken");

        // then
        assertThat(actual).isEqualTo("refresh-token");
    }

    @Test
    void 로그아웃용_액세스_토큰을_저장하면_마커를_남긴다() {
        // given

        // when
        adapter.saveAccessTokenForLogout("access-token", 1000L);

        // then
        assertThat(redisTemplate.opsForValue().get("access-token")).isEqualTo("logout");
    }

    @Test
    void 로그아웃_액세스_토큰_마커가_존재하면_true를_반환한다() {
        // given
        redisTemplate.opsForValue().set("access-token", "logout");

        // when
        boolean actual = adapter.isLogoutAccessToken("access-token");

        // then
        assertThat(actual).isTrue();
    }
}
