package com.hanyoonsoo.ordersystem.adapter.in.kafka.consumer;

import com.hanyoonsoo.ordersystem.application.order.event.OrderCreatedEvent;
import com.hanyoonsoo.ordersystem.application.order.port.in.OrderServicePort;
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
public class OrderCreatedKafkaConsumer {

    private final OrderServicePort orderService;

    @KafkaListener(
            topics = "${kafka.order-created.topic}",
            groupId = "${kafka.order-created.group-id}",
            concurrency = "${kafka.order-created.concurrency}",
            containerFactory = "orderCreatedKafkaListenerContainerFactory"
    )
    public void consume(
            OrderCreatedEvent event,
            Acknowledgment acknowledgment,
            @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
            @Header(KafkaHeaders.RECEIVED_KEY) String key
    ) {
        log.info(
                "Received order created event. topic={}, key={}, orderId={}, eventId={}",
                topic,
                key,
                event.orderId(),
                event.eventId()
        );

        orderService.handleOrderCreatedEvent(event);
        acknowledgment.acknowledge();
    }
}
