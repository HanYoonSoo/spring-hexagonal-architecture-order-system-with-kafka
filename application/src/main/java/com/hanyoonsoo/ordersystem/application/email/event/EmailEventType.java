package com.hanyoonsoo.ordersystem.application.email.event;

public enum EmailEventType {
    EMAIL_SEND_REQUESTED("email.send.requested");

    private final String value;

    EmailEventType(String value) {
        this.value = value;
    }

    public String value() {
        return value;
    }
}
