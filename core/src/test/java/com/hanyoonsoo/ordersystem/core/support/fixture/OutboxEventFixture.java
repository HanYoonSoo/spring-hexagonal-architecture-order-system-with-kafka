package com.hanyoonsoo.ordersystem.core.support.fixture;

import com.hanyoonsoo.ordersystem.core.domain.event.outbox.entity.OutboxEvent;

import java.time.LocalDateTime;

public final class OutboxEventFixture {

    private OutboxEventFixture() {
    }

    public static OutboxEvent 대기중_아웃박스이벤트() {
        LocalDateTime now = LocalDateTime.of(2026, 3, 26, 12, 0);
        return OutboxEvent.pending(
                "order.created",
                "order.created.v1",
                "1",
                "{\"orderId\":\"1\"}",
                now.minusSeconds(1),
                now
        );
    }
}
