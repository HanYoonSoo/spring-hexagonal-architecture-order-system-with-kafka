package com.hanyoonsoo.ordersystem.application.event.outbox.port.in;

import java.time.LocalDateTime;

public interface OutboxRelayServicePort {
    void append(String topic, String eventType, String eventKey, String payload, LocalDateTime occurredAt);

    void relay();
}
