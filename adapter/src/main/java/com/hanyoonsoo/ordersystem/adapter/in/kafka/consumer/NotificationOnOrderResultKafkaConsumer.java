package com.hanyoonsoo.ordersystem.adapter.in.kafka.consumer;

import com.hanyoonsoo.ordersystem.application.notification.port.in.NotificationServicePort;
import com.hanyoonsoo.ordersystem.application.order.event.OrderResultEvent;
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
public class NotificationOnOrderResultKafkaConsumer {

    private final NotificationServicePort notificationService;

    @KafkaListener(
            topics = "${app.kafka.topics.order-result}",
            groupId = "${app.kafka.consumers.notification-order-result.group-id:notification-order-result-consumer-v1}",
            concurrency = "${app.kafka.consumers.notification-order-result.concurrency:1}",
            containerFactory = "orderResultKafkaListenerContainerFactory"
    )
    public void handleNotificationOnOrderResult(
            OrderResultEvent event,
            Acknowledgment acknowledgment,
            @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
            @Header(KafkaHeaders.RECEIVED_KEY) String key,
            @Header(KafkaHeaders.GROUP_ID) String consumerGroupId
    ) {
        log.info(
                "Received order result event for notification. topic={}, key={}, orderId={}, status={}, eventId={}",
                topic,
                key,
                event.orderId(),
                event.orderStatus(),
                event.eventId()
        );
        notificationService.sendOrderResultNotification(event, consumerGroupId);
        acknowledgment.acknowledge();
    }
}
