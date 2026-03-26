package com.hanyoonsoo.ordersystem.adapter.in.kafka.consumer;

import com.hanyoonsoo.ordersystem.adapter.support.container.KafkaIntegrationTestSupporter;
import com.hanyoonsoo.ordersystem.application.order.event.OrderCreatedEvent;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;

class InventoryReservationOnOrderCreatedKafkaConsumerIntegrationTest extends KafkaIntegrationTestSupporter {

    @Test
    void 주문_생성_이벤트를_발행하면_인벤토리_서비스가_호출된다() {
        // given
        OrderCreatedEvent event = new OrderCreatedEvent(
                UUID.randomUUID(),
                "order.created",
                LocalDateTime.now(),
                UUID.randomUUID(),
                UUID.randomUUID(),
                1L,
                2L
        );

        // when
        kafkaObjectTemplate.send("order.created.v1", "1", event);
        kafkaObjectTemplate.flush();

        // then
        verify(inventoryService, timeout(10_000)).handleOrderCreated(
                argThat(actual -> actual.eventId().equals(event.eventId())
                        && actual.orderId().equals(event.orderId())
                        && actual.productId().equals(event.productId())
                        && actual.quantity().equals(event.quantity())),
                eq("order-created-consumer-v1")
        );
    }
}
