package com.hanyoonsoo.ordersystem.adapter.in.kafka.consumer;

import com.hanyoonsoo.ordersystem.application.notification.port.in.NotificationServicePort;
import com.hanyoonsoo.ordersystem.application.order.event.OrderResultEvent;
import com.hanyoonsoo.ordersystem.core.domain.order.entity.OrderStatus;
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
class NotificationOnOrderResultKafkaConsumerTest {

    @Mock
    private NotificationServicePort notificationService;

    @Mock
    private Acknowledgment acknowledgment;

    @InjectMocks
    private NotificationOnOrderResultKafkaConsumer notificationOnOrderResultKafkaConsumer;

    @Test
    void 주문결과_이벤트를_받으면_알림서비스를_호출하고_acknowledge한다() {
        // given
        OrderResultEvent event = new OrderResultEvent(
                UUID.randomUUID(),
                "order.result",
                LocalDateTime.now(),
                UUID.randomUUID(),
                UUID.randomUUID(),
                1L,
                2L,
                OrderStatus.CONFIRMED
        );

        // when
        notificationOnOrderResultKafkaConsumer.handleNotificationOnOrderResult(
                event,
                acknowledgment,
                "order.result.v1",
                "1",
                "notification-order-result-consumer-v1"
        );

        // then
        then(notificationService).should().sendOrderResultNotification(event, "notification-order-result-consumer-v1");
        then(acknowledgment).should().acknowledge();
    }
}
