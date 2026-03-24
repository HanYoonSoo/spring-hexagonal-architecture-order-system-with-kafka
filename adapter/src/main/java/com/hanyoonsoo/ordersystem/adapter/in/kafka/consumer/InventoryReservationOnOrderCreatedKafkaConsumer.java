package com.hanyoonsoo.ordersystem.adapter.in.kafka.consumer;

import com.hanyoonsoo.ordersystem.application.order.event.OrderCreatedEvent;
import com.hanyoonsoo.ordersystem.application.order.port.in.InventoryServicePort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class InventoryReservationOnOrderCreatedKafkaConsumer {

    private final InventoryServicePort inventoryService;

    @KafkaListener(
            topics = "${kafka.order-created.topic}",
            groupId = "${kafka.order-created.group-id}",
            concurrency = "${kafka.order-created.concurrency}",
            containerFactory = "orderCreatedKafkaListenerContainerFactory"
    )
    public void handleInventoryReservationOnOrderCreated(
            OrderCreatedEvent event,
            Acknowledgment acknowledgment,
            @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
            @Header(KafkaHeaders.RECEIVED_KEY) String key,
            @Header(KafkaHeaders.GROUP_ID) String consumerGroupId
    ) {
        log.info(
                "Received order created event. topic={}, key={}, groupId={}, orderId={}, eventId={}",
                topic,
                key,
                consumerGroupId,
                event.orderId(),
                event.eventId()
        );
        inventoryService.handleOrderCreated(event, consumerGroupId);
        acknowledgment.acknowledge();
    }
}
