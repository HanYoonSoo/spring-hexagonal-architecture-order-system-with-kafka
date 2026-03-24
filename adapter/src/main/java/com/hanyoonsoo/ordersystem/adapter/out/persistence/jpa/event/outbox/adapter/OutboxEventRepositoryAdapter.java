package com.hanyoonsoo.ordersystem.adapter.out.persistence.jpa.event.outbox.adapter;

import com.hanyoonsoo.ordersystem.adapter.out.persistence.jpa.event.outbox.repository.OutboxEventJpaRepository;
import com.hanyoonsoo.ordersystem.application.event.outbox.port.out.OutboxEventRepository;
import com.hanyoonsoo.ordersystem.core.domain.event.outbox.entity.OutboxEvent;
import com.hanyoonsoo.ordersystem.core.domain.event.outbox.entity.OutboxStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
@Repository
@RequiredArgsConstructor
public class OutboxEventRepositoryAdapter implements OutboxEventRepository {

    private final OutboxEventJpaRepository outboxEventJpaRepository;

    @Override
    public void save(OutboxEvent outboxEvent) {
        outboxEventJpaRepository.save(outboxEvent);
    }

    @Override
    public List<OutboxEvent> findPendingPublishTargets(LocalDateTime now, int limit) {
        return outboxEventJpaRepository.findPendingPublishTargetsWithSkipLocked(
                OutboxStatus.PENDING.name(),
                now,
                limit
        );
    }
}
