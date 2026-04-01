package com.hanyoonsoo.ordersystem.application.email.service;

import com.hanyoonsoo.ordersystem.application.email.event.EmailSendRequestedEvent;
import com.hanyoonsoo.ordersystem.application.email.port.out.EmailSender;
import com.hanyoonsoo.ordersystem.application.event.idempotency.port.out.ProcessedEventRepository;
import com.hanyoonsoo.ordersystem.core.domain.order.entity.OrderStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;

@ExtendWith(MockitoExtension.class)
class EmailServiceTest {

    @Mock
    private ProcessedEventRepository processedEventRepository;
    @Mock
    private EmailSender emailSender;

    @InjectMocks
    private EmailService emailService;

    @Test
    void 이메일발송요청이_처음_처리되면_이메일을_전송한다() {
        // given
        EmailSendRequestedEvent event = 이메일발송요청이벤트();
        given(processedEventRepository.saveIfAbsent(anyString(), any(), anyString(), any())).willReturn(true);

        // when
        emailService.sendOrderResultEmail(event, "email-send-requested-consumer-v1");

        // then
        then(emailSender).should().sendOrderResultEmail(event.email(), event);
    }

    @Test
    void 이미_처리된_이메일발송요청이면_전송하지_않는다() {
        // given
        EmailSendRequestedEvent event = 이메일발송요청이벤트();
        given(processedEventRepository.saveIfAbsent(anyString(), any(), anyString(), any())).willReturn(false);

        // when
        emailService.sendOrderResultEmail(event, "email-send-requested-consumer-v1");

        // then
        then(emailSender).should(never()).sendOrderResultEmail(anyString(), any());
    }

    private EmailSendRequestedEvent 이메일발송요청이벤트() {
        return new EmailSendRequestedEvent(
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
    }
}
