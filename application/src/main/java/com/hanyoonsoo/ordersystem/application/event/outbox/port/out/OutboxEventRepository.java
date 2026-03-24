package com.hanyoonsoo.ordersystem.application.event.outbox.port.out;

import com.hanyoonsoo.ordersystem.core.domain.event.outbox.entity.OutboxEvent;

import java.time.LocalDateTime;
import java.util.List;
public interface OutboxEventRepository {

    void save(OutboxEvent outboxEvent);

    List<OutboxEvent> findPendingPublishTargets(LocalDateTime now, int limit);
}
