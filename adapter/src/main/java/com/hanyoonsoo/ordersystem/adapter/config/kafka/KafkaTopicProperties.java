package com.hanyoonsoo.ordersystem.adapter.config.kafka;

import com.hanyoonsoo.ordersystem.application.event.outbox.model.EventTopicKey;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "app.kafka.topics")
public class KafkaTopicProperties {

    private String orderCreated = "order.created.v1";
    private String orderResult = "order.result.v1";
    private String emailSendRequested = "email.send.requested.v1";

    public String topicOf(EventTopicKey topicKey) {
        return switch (topicKey) {
            case ORDER_CREATED -> orderCreated;
            case ORDER_RESULT -> orderResult;
            case EMAIL_SEND_REQUESTED -> emailSendRequested;
        };
    }
}
