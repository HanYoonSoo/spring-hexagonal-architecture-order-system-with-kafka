package com.hanyoonsoo.ordersystem.adapter.in.kafka.consumer;

import com.hanyoonsoo.ordersystem.application.order.event.OrderCreatedEvent;
import com.hanyoonsoo.ordersystem.application.order.port.in.InventoryServicePort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.support.Acknowledgment;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class InventoryReservationOnOrderCreatedKafkaConsumerTest {

    @Mock
    private InventoryServicePort inventoryService;
    @Mock
    private Acknowledgment acknowledgment;
    @InjectMocks
    private InventoryReservationOnOrderCreatedKafkaConsumer consumer;

    @Test
    void 주문_생성_이벤트를_처리하고_acknowledge를_호출한다() {
        // given
        OrderCreatedEvent event = new OrderCreatedEvent(UUID.randomUUID(), "order.created", LocalDateTime.now(), UUID.randomUUID(), UUID.randomUUID(), 1L, 2L);

        // when
        consumer.handleInventoryReservationOnOrderCreated(event, acknowledgment, "order.created.v1", "1", "inventory-order-created-v1");

        // then
        then(inventoryService).should().handleOrderCreated(event, "inventory-order-created-v1");
        then(acknowledgment).should().acknowledge();
    }
}
