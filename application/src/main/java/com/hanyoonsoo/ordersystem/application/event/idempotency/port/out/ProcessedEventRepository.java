package com.hanyoonsoo.ordersystem.application.event.idempotency.port.out;

import java.time.LocalDateTime;
import java.util.UUID;

public interface ProcessedEventRepository {

    boolean saveIfAbsent(String consumerGroupId, UUID eventId, String eventType, LocalDateTime processedAt);
}
