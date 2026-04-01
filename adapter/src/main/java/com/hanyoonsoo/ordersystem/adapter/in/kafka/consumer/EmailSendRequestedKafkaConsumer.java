package com.hanyoonsoo.ordersystem.adapter.in.kafka.consumer;

import com.hanyoonsoo.ordersystem.application.email.event.EmailSendRequestedEvent;
import com.hanyoonsoo.ordersystem.application.email.port.in.EmailServicePort;
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
public class EmailSendRequestedKafkaConsumer {

    private final EmailServicePort emailService;

    @KafkaListener(
            topics = "${app.kafka.topics.email-send-requested}",
            groupId = "${app.kafka.consumers.email-send-requested.group-id:email-send-requested-consumer-v1}",
            concurrency = "${app.kafka.consumers.email-send-requested.concurrency:1}",
            containerFactory = "emailSendRequestedKafkaListenerContainerFactory"
    )
    public void handleEmailSendRequested(
            EmailSendRequestedEvent event,
            Acknowledgment acknowledgment,
            @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
            @Header(KafkaHeaders.RECEIVED_KEY) String key,
            @Header(KafkaHeaders.GROUP_ID) String consumerGroupId
    ) {
        log.info(
                "Received email send requested event. topic={}, key={}, orderId={}, userId={}, eventId={}",
                topic,
                key,
                event.orderId(),
                event.userId(),
                event.eventId()
        );
        emailService.sendOrderResultEmail(event, consumerGroupId);
        acknowledgment.acknowledge();
    }
}
