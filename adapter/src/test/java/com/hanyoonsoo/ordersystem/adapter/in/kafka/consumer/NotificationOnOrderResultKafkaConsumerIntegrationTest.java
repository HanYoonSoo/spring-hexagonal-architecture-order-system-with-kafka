package com.hanyoonsoo.ordersystem.adapter.in.kafka.consumer;

import com.hanyoonsoo.ordersystem.adapter.support.container.KafkaIntegrationTestSupporter;
import com.hanyoonsoo.ordersystem.application.order.event.OrderResultEvent;
import com.hanyoonsoo.ordersystem.core.domain.order.entity.OrderStatus;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;

class NotificationOnOrderResultKafkaConsumerIntegrationTest extends KafkaIntegrationTestSupporter {

    @Test
    void 주문결과_이벤트를_발행하면_알림서비스가_호출된다() {
        // given
        OrderResultEvent event = new OrderResultEvent(
                UUID.randomUUID(),
                "order.result",
                LocalDateTime.now(),
                UUID.randomUUID(),
                UUID.randomUUID(),
                1L,
                2L,
                OrderStatus.REJECTED_OUT_OF_STOCK
        );

        // when
        kafkaObjectTemplate.send("order.result.v1", event.orderId().toString(), event);
        kafkaObjectTemplate.flush();

        // then
        verify(notificationService, timeout(10_000)).sendOrderResultNotification(
                argThat(actual -> actual.eventId().equals(event.eventId())
                        && actual.orderId().equals(event.orderId())
                        && actual.userId().equals(event.userId())
                        && actual.orderStatus().equals(event.orderStatus())),
                eq("notification-order-result-consumer-v1")
        );
    }
}
