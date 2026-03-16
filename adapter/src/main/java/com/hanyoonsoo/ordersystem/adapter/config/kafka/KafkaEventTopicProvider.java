package com.hanyoonsoo.ordersystem.adapter.config.kafka;

import com.hanyoonsoo.ordersystem.application.outbox.model.EventTopicKey;
import com.hanyoonsoo.ordersystem.application.outbox.port.out.EventTopicProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class KafkaEventTopicProvider implements EventTopicProvider {

    private final Environment environment;

    @Override
    public String topicOf(EventTopicKey topicKey) {
        return environment.getProperty(topicKey.propertyPath(), topicKey.defaultTopic());
    }
}
