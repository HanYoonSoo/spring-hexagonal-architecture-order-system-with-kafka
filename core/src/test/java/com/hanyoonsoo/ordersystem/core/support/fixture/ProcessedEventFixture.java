package com.hanyoonsoo.ordersystem.core.support.fixture;

import com.hanyoonsoo.ordersystem.core.domain.event.idempotency.entity.ProcessedEvent;

import java.time.LocalDateTime;
import java.util.UUID;

public final class ProcessedEventFixture {

    private ProcessedEventFixture() {
    }

    public static ProcessedEvent 처리된_이벤트() {
        return ProcessedEvent.of(
                "inventory-order-created-v1",
                UUID.randomUUID(),
                "order.created",
                LocalDateTime.of(2026, 3, 26, 12, 0)
        );
    }
}
