package com.hanyoonsoo.ordersystem.application.notification.service;

import com.hanyoonsoo.ordersystem.application.email.event.EmailEventType;
import com.hanyoonsoo.ordersystem.application.email.event.EmailSendRequestedEvent;
import com.hanyoonsoo.ordersystem.application.event.idempotency.port.out.ProcessedEventRepository;
import com.hanyoonsoo.ordersystem.application.event.outbox.model.EventTopicKey;
import com.hanyoonsoo.ordersystem.application.event.outbox.port.in.OutboxRelayServicePort;
import com.hanyoonsoo.ordersystem.application.event.outbox.port.out.EventTopicProvider;
import com.hanyoonsoo.ordersystem.application.notification.port.in.NotificationServicePort;
import com.hanyoonsoo.ordersystem.application.order.event.OrderResultEvent;
import com.hanyoonsoo.ordersystem.application.user.port.out.UserRepository;
import com.hanyoonsoo.ordersystem.common.utils.ObjectMapperUtils;
import com.hanyoonsoo.ordersystem.core.domain.user.entity.UserCredential;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class NotificationService implements NotificationServicePort {

    private final ProcessedEventRepository processedEventRepository;
    private final UserRepository userRepository;
    private final OutboxRelayServicePort outboxRelayService;
    private final EventTopicProvider eventTopicProvider;
    private final ObjectMapperUtils objectMapperUtils;

    @Override
    @Transactional
    public void sendOrderResultNotification(OrderResultEvent event, String consumerGroupId) {
        boolean firstProcessed = processedEventRepository.saveIfAbsent(
                consumerGroupId,
                event.eventId(),
                event.eventType(),
                LocalDateTime.now()
        );

        if (!firstProcessed) {
            return;
        }

        UserCredential userCredential =
                userRepository.findUserCredentialByUserIdAndDeletedAtIsNull(event.userId()).orElse(null);

        if (userCredential == null) {
            return;
        }

        EmailSendRequestedEvent emailSendRequestedEvent = new EmailSendRequestedEvent(
                UUID.randomUUID(),
                EmailEventType.EMAIL_SEND_REQUESTED.value(),
                LocalDateTime.now(),
                event.orderId(),
                event.userId(),
                userCredential.getLoginId(),
                event.productId(),
                event.quantity(),
                event.orderStatus()
        );

        outboxRelayService.append(
                eventTopicProvider.topicOf(EventTopicKey.EMAIL_SEND_REQUESTED),
                emailSendRequestedEvent.eventType(),
                event.orderId().toString(),
                objectMapperUtils.writeValueAsString(emailSendRequestedEvent),
                emailSendRequestedEvent.occurredAt()
        );
    }
}
