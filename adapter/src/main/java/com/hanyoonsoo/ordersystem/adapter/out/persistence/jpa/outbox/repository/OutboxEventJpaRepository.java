package com.hanyoonsoo.ordersystem.adapter.out.persistence.jpa.outbox.repository;

import com.hanyoonsoo.ordersystem.core.domain.outbox.entity.OutboxEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface OutboxEventJpaRepository extends JpaRepository<OutboxEvent, UUID> {

    @Query(value = """
            select *
            from outbox_event
            where status = :status
              and next_retry_at <= :now
            order by created_at asc
            limit :limit
            for update skip locked
            """, nativeQuery = true)
    List<OutboxEvent> findPendingPublishTargetsWithSkipLocked(
            @Param("status") String status,
            @Param("now") LocalDateTime now,
            @Param("limit") int limit
    );
}
