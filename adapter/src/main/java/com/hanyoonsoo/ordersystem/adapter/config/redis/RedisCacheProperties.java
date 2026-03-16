package com.hanyoonsoo.ordersystem.adapter.config.redis;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "redis.cache")
public class RedisCacheProperties {

    private long defaultTtlSeconds = 3600;
    private Map<String, Long> ttlSeconds = new HashMap<>();

    public Duration ttlFor(CacheType cacheType) {
        return ttlFor(cacheType.getCacheName());
    }

    public Duration ttlFor(String cacheName) {
        long ttl = ttlSeconds.getOrDefault(cacheName, defaultTtlSeconds);
        return Duration.ofSeconds(Math.max(1L, ttl));
    }
}
