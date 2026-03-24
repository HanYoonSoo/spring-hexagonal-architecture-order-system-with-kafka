package com.hanyoonsoo.ordersystem.core.domain.event.idempotency.entity;

import com.hanyoonsoo.ordersystem.core.domain.common.BaseTimeEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Entity
@Table(
        name = "processed_event",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_processed_event_group_event", columnNames = {"consumer_group_id", "event_id"})
        }
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProcessedEvent extends BaseTimeEntity {

    @Id
    @Column(name = "id", nullable = false)
    private UUID id;

    @Column(name = "consumer_group_id", nullable = false, length = 100)
    private String consumerGroupId;

    @Column(name = "event_id", nullable = false)
    private UUID eventId;

    @Column(name = "event_type", nullable = false, length = 100)
    private String eventType;

    @Column(name = "processed_at", nullable = false)
    private LocalDateTime processedAt;

    private ProcessedEvent(String consumerGroupId, UUID eventId, String eventType, LocalDateTime processedAt) {
        this.id = UUID.randomUUID();
        this.consumerGroupId = consumerGroupId;
        this.eventId = eventId;
        this.eventType = eventType;
        this.processedAt = processedAt;
    }

    public static ProcessedEvent of(String consumerGroupId, UUID eventId, String eventType, LocalDateTime processedAt) {
        return new ProcessedEvent(consumerGroupId, eventId, eventType, processedAt);
    }
}
