package com.hanyoonsoo.ordersystem.adapter.in.kafka.consumer;

import com.hanyoonsoo.ordersystem.application.email.event.EmailSendRequestedEvent;
import com.hanyoonsoo.ordersystem.application.email.port.in.EmailServicePort;
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
class EmailSendRequestedKafkaConsumerTest {

    @Mock
    private EmailServicePort emailService;

    @Mock
    private Acknowledgment acknowledgment;

    @InjectMocks
    private EmailSendRequestedKafkaConsumer emailSendRequestedKafkaConsumer;

    @Test
    void 이메일발송요청_이벤트를_받으면_이메일서비스를_호출하고_acknowledge한다() {
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
        emailSendRequestedKafkaConsumer.handleEmailSendRequested(
                event,
                acknowledgment,
                "email.send.requested.v1",
                event.orderId().toString(),
                "email-send-requested-consumer-v1"
        );

        // then
        then(emailService).should().sendOrderResultEmail(event, "email-send-requested-consumer-v1");
        then(acknowledgment).should().acknowledge();
    }
}
