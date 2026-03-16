package com.hanyoonsoo.ordersystem.adapter.out.persistence.jpa.outbox.repository;

import com.hanyoonsoo.ordersystem.core.domain.outbox.entity.OutboxEvent;
import com.hanyoonsoo.ordersystem.core.domain.outbox.entity.OutboxStatus;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface OutboxEventJpaRepository extends JpaRepository<OutboxEvent, UUID> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
            select outbox
            from OutboxEvent outbox
            where outbox.status = :status
              and outbox.nextRetryAt <= :now
            order by outbox.createdAt asc
            """)
    List<OutboxEvent> findOutboxEventsByStatusAndNextRetryAtLessThanEqualOrderByCreatedAtAsc(
            @Param("status") OutboxStatus status,
            @Param("now") LocalDateTime now,
            Pageable pageable
    );
}
