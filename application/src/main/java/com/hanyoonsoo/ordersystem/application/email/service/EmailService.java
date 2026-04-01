package com.hanyoonsoo.ordersystem.application.email.service;

import com.hanyoonsoo.ordersystem.application.email.event.EmailSendRequestedEvent;
import com.hanyoonsoo.ordersystem.application.email.port.in.EmailServicePort;
import com.hanyoonsoo.ordersystem.application.email.port.out.EmailSender;
import com.hanyoonsoo.ordersystem.application.event.idempotency.port.out.ProcessedEventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class EmailService implements EmailServicePort {

    private final ProcessedEventRepository processedEventRepository;
    private final EmailSender emailSender;

    @Override
    @Transactional
    public void sendOrderResultEmail(EmailSendRequestedEvent event, String consumerGroupId) {
        boolean firstProcessed = processedEventRepository.saveIfAbsent(
                consumerGroupId,
                event.eventId(),
                event.eventType(),
                LocalDateTime.now()
        );
        if (!firstProcessed) {
            return;
        }

        emailSender.sendOrderResultEmail(event.email(), event);
    }
}
