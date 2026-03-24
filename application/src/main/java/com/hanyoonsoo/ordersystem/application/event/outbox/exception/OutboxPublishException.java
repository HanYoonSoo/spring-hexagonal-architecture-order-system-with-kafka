package com.hanyoonsoo.ordersystem.application.event.outbox.exception;

public class OutboxPublishException extends RuntimeException {

    public OutboxPublishException(String message, Throwable cause) {
        super(message, cause);
    }
}
