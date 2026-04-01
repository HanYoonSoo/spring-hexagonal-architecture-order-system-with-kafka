package com.hanyoonsoo.ordersystem.application.support.fixture;

import com.hanyoonsoo.ordersystem.application.order.event.OrderCreatedEvent;
import com.hanyoonsoo.ordersystem.application.order.event.OrderResultEvent;
import com.hanyoonsoo.ordersystem.core.domain.order.entity.OrderStatus;
import com.hanyoonsoo.ordersystem.core.domain.event.outbox.entity.OutboxEvent;

import java.time.LocalDateTime;
import java.util.UUID;

public final class EventFixture {

    private EventFixture() {
    }

    public static OrderCreatedEvent 주문생성이벤트() {
        return new OrderCreatedEvent(
                UUID.randomUUID(),
                "order.created",
                LocalDateTime.of(2026, 3, 26, 12, 0),
                UUID.randomUUID(),
                UUID.randomUUID(),
                1L,
                2L
        );
    }

    public static OutboxEvent 아웃박스이벤트() {
        return OutboxEvent.pending(
                "order.created",
                "order.created.v1",
                "1",
                "{\"orderId\":\"1\"}",
                LocalDateTime.of(2026, 3, 26, 12, 0),
                LocalDateTime.of(2026, 3, 26, 12, 0)
        );
    }

    public static OrderResultEvent 주문결과이벤트() {
        return new OrderResultEvent(
                UUID.randomUUID(),
                "order.result",
                LocalDateTime.of(2026, 3, 26, 12, 10),
                UUID.randomUUID(),
                UUID.randomUUID(),
                1L,
                2L,
                OrderStatus.CONFIRMED
        );
    }
}
