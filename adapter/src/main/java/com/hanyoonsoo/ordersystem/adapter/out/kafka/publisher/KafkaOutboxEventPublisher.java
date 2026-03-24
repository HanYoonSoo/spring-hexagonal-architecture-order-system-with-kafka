package com.hanyoonsoo.ordersystem.adapter.out.kafka.publisher;

import com.hanyoonsoo.ordersystem.application.event.outbox.exception.OutboxPublishException;
import com.hanyoonsoo.ordersystem.application.event.outbox.port.out.OutboxEventPublisher;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutionException;

@Slf4j
@Component
public class KafkaOutboxEventPublisher implements OutboxEventPublisher {

    private final KafkaTemplate<String, String> kafkaStringTemplate;

    public KafkaOutboxEventPublisher(
            @Qualifier("kafkaStringTemplate") KafkaTemplate<String, String> kafkaStringTemplate
    ) {
        this.kafkaStringTemplate = kafkaStringTemplate;
    }

    @Override
    public void publish(String topic, String key, String payload) {
        try {
            var result = kafkaStringTemplate.send(topic, key, payload).get();
            log.info(
                    "Outbox event published. topic={}, partition={}, offset={}, key={}",
                    topic,
                    result.getRecordMetadata().partition(),
                    result.getRecordMetadata().offset(),
                    key
            );
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            throw new OutboxPublishException(
                    "Interrupted while publishing outbox event. topic=%s, key=%s".formatted(topic, key),
                    exception
            );
        } catch (ExecutionException exception) {
            throw new OutboxPublishException(
                    "Failed to publish outbox event. topic=%s, key=%s".formatted(topic, key),
                    exception
            );
        }
    }
}
