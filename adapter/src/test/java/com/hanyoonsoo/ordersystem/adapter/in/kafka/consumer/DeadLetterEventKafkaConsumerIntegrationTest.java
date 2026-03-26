package com.hanyoonsoo.ordersystem.adapter.in.kafka.consumer;

import com.hanyoonsoo.ordersystem.adapter.support.container.KafkaIntegrationTestSupporter;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.internals.RecordHeader;
import org.junit.jupiter.api.Test;
import org.springframework.kafka.support.KafkaHeaders;

import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.UUID;

import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;

class DeadLetterEventKafkaConsumerIntegrationTest extends KafkaIntegrationTestSupporter {

    @Test
    void DLT_이벤트를_발행하면_주문_실패_처리_서비스가_호출된다() {
        // given
        UUID orderId = UUID.randomUUID();
        ProducerRecord<String, Object> record = new ProducerRecord<>(
                "order.created.v1.dlt",
                "1",
                Map.of("orderId", orderId.toString())
        );
        record.headers().add(new RecordHeader(
                KafkaHeaders.DLT_ORIGINAL_TOPIC,
                "order.created.v1".getBytes(StandardCharsets.UTF_8)
        ));

        // when
        kafkaObjectTemplate.send(record);
        kafkaObjectTemplate.flush();

        // then
        verify(orderService, timeout(10_000)).handleOrderCreatedDltEvent(orderId);
    }
}
