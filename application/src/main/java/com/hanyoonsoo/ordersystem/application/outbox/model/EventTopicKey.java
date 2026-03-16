package com.hanyoonsoo.ordersystem.application.outbox.model;

public enum EventTopicKey {
    ORDER_CREATED("kafka.order-created.topic", "order.created.v1");

    private final String propertyPath;
    private final String defaultTopic;

    EventTopicKey(String propertyPath, String defaultTopic) {
        this.propertyPath = propertyPath;
        this.defaultTopic = defaultTopic;
    }

    public String propertyPath() {
        return propertyPath;
    }

    public String defaultTopic() {
        return defaultTopic;
    }
}
