package com.hanyoonsoo.ordersystem.adapter.out.persistence.jpa.event.idempotency.repository;

import com.hanyoonsoo.ordersystem.core.domain.event.idempotency.entity.ProcessedEvent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ProcessedEventJpaRepository extends JpaRepository<ProcessedEvent, UUID> {
}
