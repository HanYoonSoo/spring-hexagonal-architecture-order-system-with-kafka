package com.hanyoonsoo.ordersystem.application.event.outbox.port.out;

public interface OutboxEventPublisher {

    void publish(String topic, String key, String payload);
}
