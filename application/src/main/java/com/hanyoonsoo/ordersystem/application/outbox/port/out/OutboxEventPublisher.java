package com.hanyoonsoo.ordersystem.application.outbox.port.out;

public interface OutboxEventPublisher {

    void publish(String topic, String key, String payload);
}
