package com.hanyoonsoo.ordersystem.adapter.config.redis;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CacheType {
    STOCK("stock"),
    RECENTLY_VIEWED_PRODUCT("recently-viewed-product");

    private final String cacheName;
}
