package com.hanyoonsoo.ordersystem.application.order.event;

public enum OrderEventType {
    ORDER_CREATED("order.created"),
    ORDER_RESULT("order.result");

    private final String value;

    OrderEventType(String value) {
        this.value = value;
    }

    public String value() {
        return value;
    }
}
