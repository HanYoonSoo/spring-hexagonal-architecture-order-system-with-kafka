package com.hanyoonsoo.ordersystem.adapter.out.kafka.publisher;

import com.hanyoonsoo.ordersystem.adapter.support.container.KafkaIntegrationTestSupporter;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.Duration;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class KafkaOutboxEventPublisherIntegrationTest extends KafkaIntegrationTestSupporter {

    @Autowired
    private KafkaOutboxEventPublisher kafkaOutboxEventPublisher;

    @Test
    void 아웃박스_이벤트를_발행하면_카프카_토픽에_레코드가_저장된다() {
        // given
        String topic = "order.created.v1";
        String key = "product-1";
        String payload = "{\"event\":\"order.created\"}";

        try (KafkaConsumer<String, String> consumer = new KafkaConsumer<>(consumerProperties())) {
            consumer.subscribe(java.util.List.of(topic));
            consumer.poll(Duration.ofSeconds(1));

            // when
            kafkaOutboxEventPublisher.publish(topic, key, payload);

            // then
            ConsumerRecord<String, String> actualRecord = pollSingleRecord(consumer);
            assertThat(actualRecord.topic()).isEqualTo(topic);
            assertThat(actualRecord.key()).isEqualTo(key);
            assertThat(actualRecord.value()).isEqualTo(payload);
        }
    }

    private Map<String, Object> consumerProperties() {
        return Map.of(
                ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaBootstrapServers(),
                ConsumerConfig.GROUP_ID_CONFIG, "publisher-test-" + UUID.randomUUID(),
                ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest",
                ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class,
                ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class
        );
    }

    private ConsumerRecord<String, String> pollSingleRecord(KafkaConsumer<String, String> consumer) {
        long deadline = System.currentTimeMillis() + 10_000;
        while (System.currentTimeMillis() < deadline) {
            ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(500));
            if (!records.isEmpty()) {
                return records.iterator().next();
            }
        }
        throw new AssertionError("지정한 시간 안에 카프카 레코드를 수신하지 못했습니다.");
    }
}
