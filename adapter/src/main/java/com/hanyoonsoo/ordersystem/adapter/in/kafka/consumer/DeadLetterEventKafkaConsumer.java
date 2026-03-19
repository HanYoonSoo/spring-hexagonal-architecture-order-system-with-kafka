package com.hanyoonsoo.ordersystem.adapter.in.kafka.consumer;

import com.hanyoonsoo.ordersystem.adapter.config.kafka.KafkaConsumerErrorHandlerProperties;
import com.hanyoonsoo.ordersystem.application.order.port.in.OrderServicePort;
import com.hanyoonsoo.ordersystem.application.outbox.model.EventTopicKey;
import com.hanyoonsoo.ordersystem.application.outbox.port.out.EventTopicProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class DeadLetterEventKafkaConsumer {

    private final OrderServicePort orderService;
    private final EventTopicProvider eventTopicProvider;
    private final KafkaConsumerErrorHandlerProperties kafkaConsumerErrorHandlerProperties;

    @KafkaListener(
            topicPattern = ".*\\.dlt",
            groupId = "${kafka.dlt.group-id:order-dlt-consumer-v1}",
            containerFactory = "dltKafkaListenerContainerFactory"
    )
    public void handleDeadLetterEvent(
            Map<String, Object> payload,
            Acknowledgment acknowledgment,
            @Header(KafkaHeaders.RECEIVED_TOPIC) String dltTopic,
            @Header(name = KafkaHeaders.DLT_ORIGINAL_TOPIC, required = false) String originalTopic
    ) {
        String resolvedOriginalTopic = resolveOriginalTopic(dltTopic, originalTopic);
        EventTopicKey topicKey = resolveTopicKey(resolvedOriginalTopic);
        if (topicKey == null) {
            log.info("Skipping DLT event. dltTopic={}, originalTopic={}", dltTopic, resolvedOriginalTopic);
            acknowledgment.acknowledge();
            return;
        }

        switch (topicKey) {
            case ORDER_CREATED -> {
                UUID orderId = extractOrderId(payload);
                if (orderId == null) {
                    log.warn("Failed to parse orderId from DLT payload. dltTopic={}, originalTopic={}", dltTopic, resolvedOriginalTopic);
                    acknowledgment.acknowledge();
                    return;
                }
                orderService.handleOrderCreatedDltEvent(orderId);
                log.warn("Order marked failed from DLT event. orderId={}, dltTopic={}, originalTopic={}", orderId, dltTopic, resolvedOriginalTopic);
            }
        }
        acknowledgment.acknowledge();
    }

    private UUID extractOrderId(Map<String, Object> payload) {
        if (payload == null) {
            return null;
        }
        Object orderId = payload.get("orderId");

        return orderId != null ? UUID.fromString(orderId.toString()) : null;
    }

    private String resolveOriginalTopic(String dltTopic, String originalTopicHeader) {
        if (originalTopicHeader != null && !originalTopicHeader.isBlank()) {
            return originalTopicHeader;
        }
        String dltSuffix = kafkaConsumerErrorHandlerProperties.getDltSuffix();
        if (dltSuffix == null || dltSuffix.isBlank() || dltTopic == null || !dltTopic.endsWith(dltSuffix)) {
            return null;
        }
        return dltTopic.substring(0, dltTopic.length() - dltSuffix.length());
    }

    private EventTopicKey resolveTopicKey(String originalTopic) {
        if (originalTopic == null || originalTopic.isBlank()) {
            return null;
        }
        for (EventTopicKey topicKey : EventTopicKey.values()) {
            String configuredTopic = eventTopicProvider.topicOf(topicKey);
            if (originalTopic.equals(configuredTopic)) {
                return topicKey;
            }
        }
        return null;
    }
}
