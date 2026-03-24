package com.hanyoonsoo.ordersystem.application.event.outbox.service;

import com.hanyoonsoo.ordersystem.application.event.outbox.config.OutboxRelayProperties;
import com.hanyoonsoo.ordersystem.application.event.outbox.port.in.OutboxRelayServicePort;
import com.hanyoonsoo.ordersystem.application.event.outbox.port.out.OutboxEventPublisher;
import com.hanyoonsoo.ordersystem.application.event.outbox.port.out.OutboxEventRepository;
import com.hanyoonsoo.ordersystem.core.domain.event.outbox.entity.OutboxEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class OutboxRelayService implements OutboxRelayServicePort {

    private final OutboxEventRepository outboxEventRepository;
    private final OutboxEventPublisher outboxEventPublisher;
    private final OutboxRelayProperties outboxRelayProperties;

    @Override
    @Transactional
    public void append(
            String topic,
            String eventType,
            String eventKey,
            String payload,
            LocalDateTime occurredAt
    ) {
        log.info(
                "Appending outbox event. topic={}, eventType={}, eventKey={}, payloadLength={}, payloadPreview={}",
                topic,
                eventType,
                eventKey,
                payload == null ? 0 : payload.length(),
                preview(payload)
        );

        OutboxEvent outboxEvent = OutboxEvent.pending(
                eventType,
                topic,
                eventKey,
                payload,
                occurredAt,
                LocalDateTime.now()
        );
        outboxEventRepository.save(outboxEvent);
    }

    @Override
    @Scheduled(fixedDelayString = "${outbox.relay.fixed-delay-millis:500}")
    @Transactional
    public void relay() {
        if (!outboxRelayProperties.isEnabled()) {
            return;
        }

        List<OutboxEvent> outboxEvents = outboxEventRepository.findPendingPublishTargets(
                LocalDateTime.now(),
                outboxRelayProperties.getBatchSize()
        );
        if (outboxEvents.isEmpty()) {
            return;
        }

        for (OutboxEvent outboxEvent : outboxEvents) {
            try {
                outboxEventPublisher.publish(
                        outboxEvent.getTopic(),
                        outboxEvent.getEventKey(),
                        outboxEvent.getPayload()
                );
                outboxEvent.markPublished(LocalDateTime.now());
            } catch (Exception exception) {
                outboxEvent.markRetry(
                        LocalDateTime.now().plus(Duration.ofMillis(outboxRelayProperties.getRetryBackoffMillis())),
                        exception.getMessage(),
                        outboxRelayProperties.getMaxRetryCount()
                );
                log.error(
                        "Failed to relay outbox event. outboxId={}, eventType={}, retryCount={}",
                        outboxEvent.getId(),
                        outboxEvent.getEventType(),
                        outboxEvent.getRetryCount(),
                        exception
                );
            }
        }
    }

    private String preview(String payload) {
        if (payload == null) {
            return "null";
        }
        int maxLength = 120;
        if (payload.length() <= maxLength) {
            return payload;
        }
        return payload.substring(0, maxLength) + "...";
    }
}
