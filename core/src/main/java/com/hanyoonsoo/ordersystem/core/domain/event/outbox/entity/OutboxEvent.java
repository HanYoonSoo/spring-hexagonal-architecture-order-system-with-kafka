package com.hanyoonsoo.ordersystem.core.domain.event.outbox.entity;

import com.hanyoonsoo.ordersystem.core.domain.common.BaseTimeEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Entity
@Table(name = "outbox_event")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OutboxEvent extends BaseTimeEntity {

    @Id
    @Column(name = "id", nullable = false)
    private UUID id;

    @Column(name = "event_type", nullable = false, length = 100)
    private String eventType;

    @Column(name = "topic", nullable = false, length = 255)
    private String topic;

    @Column(name = "event_key", length = 255)
    private String eventKey;

    @Column(name = "payload", nullable = false, columnDefinition = "text")
    private String payload;

    @Column(name = "occurred_at", nullable = false)
    private LocalDateTime occurredAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private OutboxStatus status;

    @Column(name = "retry_count", nullable = false)
    private int retryCount;

    @Column(name = "next_retry_at", nullable = false)
    private LocalDateTime nextRetryAt;

    @Column(name = "published_at")
    private LocalDateTime publishedAt;

    @Column(name = "last_error", length = 1000)
    private String lastError;

    private OutboxEvent(
            String eventType,
            String topic,
            String eventKey,
            String payload,
            LocalDateTime occurredAt,
            LocalDateTime now
    ) {
        this.id = UUID.randomUUID();
        this.eventType = eventType;
        this.topic = topic;
        this.eventKey = eventKey;
        this.payload = payload;
        this.occurredAt = occurredAt;
        this.status = OutboxStatus.PENDING;
        this.retryCount = 0;
        this.nextRetryAt = now;
    }

    public static OutboxEvent pending(
            String eventType,
            String topic,
            String eventKey,
            String payload,
            LocalDateTime occurredAt,
            LocalDateTime now
    ) {
        return new OutboxEvent(eventType, topic, eventKey, payload, occurredAt, now);
    }

    public void markPublished(LocalDateTime publishedAt) {
        this.status = OutboxStatus.PUBLISHED;
        this.publishedAt = publishedAt;
        this.lastError = null;
    }

    public void markRetry(LocalDateTime nextRetryAt, String errorMessage, int maxRetryCount) {
        this.retryCount = this.retryCount + 1;
        this.lastError = truncate(errorMessage);
        if (this.retryCount >= maxRetryCount) {
            this.status = OutboxStatus.FAILED;
            return;
        }
        this.status = OutboxStatus.PENDING;
        this.nextRetryAt = nextRetryAt;
    }

    private String truncate(String errorMessage) {
        if (errorMessage == null) {
            return null;
        }
        if (errorMessage.length() <= 1000) {
            return errorMessage;
        }
        return errorMessage.substring(0, 1000);
    }
}
