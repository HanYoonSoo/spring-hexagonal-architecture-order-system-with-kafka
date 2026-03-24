package com.hanyoonsoo.ordersystem.application.event.outbox.port.out;

import com.hanyoonsoo.ordersystem.application.event.outbox.model.EventTopicKey;

public interface EventTopicProvider {

    String topicOf(EventTopicKey topicKey);
}
