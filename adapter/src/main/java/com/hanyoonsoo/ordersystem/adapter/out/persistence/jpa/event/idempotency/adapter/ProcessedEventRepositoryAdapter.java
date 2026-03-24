package com.hanyoonsoo.ordersystem.adapter.out.persistence.jpa.event.idempotency.adapter;

import com.hanyoonsoo.ordersystem.adapter.out.persistence.jpa.event.idempotency.repository.ProcessedEventJpaRepository;
import com.hanyoonsoo.ordersystem.application.event.idempotency.port.out.ProcessedEventRepository;
import com.hanyoonsoo.ordersystem.core.domain.event.idempotency.entity.ProcessedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class ProcessedEventRepositoryAdapter implements ProcessedEventRepository {

    private final ProcessedEventJpaRepository processedEventJpaRepository;

    @Override
    public boolean saveIfAbsent(String consumerGroupId, UUID eventId, String eventType, LocalDateTime processedAt) {
        try {
            processedEventJpaRepository.saveAndFlush(
                    ProcessedEvent.of(consumerGroupId, eventId, eventType, processedAt)
            );
            return true;
        } catch (DataIntegrityViolationException exception) {
            return false;
        }
    }
}
