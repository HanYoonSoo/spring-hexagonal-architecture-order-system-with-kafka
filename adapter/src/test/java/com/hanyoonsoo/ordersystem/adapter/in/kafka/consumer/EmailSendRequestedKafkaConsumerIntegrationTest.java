package com.hanyoonsoo.ordersystem.adapter.in.kafka.consumer;

import com.hanyoonsoo.ordersystem.adapter.support.container.KafkaIntegrationTestSupporter;
import com.hanyoonsoo.ordersystem.application.email.event.EmailSendRequestedEvent;
import com.hanyoonsoo.ordersystem.core.domain.order.entity.OrderStatus;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;

class EmailSendRequestedKafkaConsumerIntegrationTest extends KafkaIntegrationTestSupporter {

    @Test
    void 이메일발송요청_이벤트를_발행하면_이메일서비스가_호출된다() {
        // given
        EmailSendRequestedEvent event = new EmailSendRequestedEvent(
                UUID.randomUUID(),
                "email.send.requested",
                LocalDateTime.now(),
                UUID.randomUUID(),
                UUID.randomUUID(),
                "user@example.com",
                1L,
                2L,
                OrderStatus.CONFIRMED
        );

        // when
        kafkaObjectTemplate.send("email.send.requested.v1", event.orderId().toString(), event);
        kafkaObjectTemplate.flush();

        // then
        verify(emailService, timeout(10_000)).sendOrderResultEmail(
                argThat(actual -> actual.eventId().equals(event.eventId())
                        && actual.orderId().equals(event.orderId())
                        && actual.email().equals(event.email())
                        && actual.orderStatus().equals(event.orderStatus())),
                eq("email-send-requested-consumer-v1")
        );
    }
}
