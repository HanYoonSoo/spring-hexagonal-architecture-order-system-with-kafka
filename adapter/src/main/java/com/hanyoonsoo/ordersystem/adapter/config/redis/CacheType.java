package com.hanyoonsoo.ordersystem.adapter.config.redis;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CacheType {
    STOCK("stock");

    private final String cacheName;
}
