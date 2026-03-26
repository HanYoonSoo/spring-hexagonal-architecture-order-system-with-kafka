package com.hanyoonsoo.ordersystem.adapter.support.fixture;

import com.hanyoonsoo.ordersystem.core.domain.order.entity.Order;

import java.util.UUID;

public final class OrderFixture {

    private OrderFixture() {
    }

    public static Order 대기중_주문() {
        return Order.pending(UUID.randomUUID(), 1L, 2L);
    }
}
