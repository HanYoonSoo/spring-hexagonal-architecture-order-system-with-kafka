package com.hanyoonsoo.ordersystem.application.outbox.port.out;

import com.hanyoonsoo.ordersystem.application.outbox.model.EventTopicKey;

public interface EventTopicProvider {

    String topicOf(EventTopicKey topicKey);
}
