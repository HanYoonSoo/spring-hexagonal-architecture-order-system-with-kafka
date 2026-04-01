package com.hanyoonsoo.ordersystem.application.email.port.in;

import com.hanyoonsoo.ordersystem.application.email.event.EmailSendRequestedEvent;

public interface EmailServicePort {

    void sendOrderResultEmail(EmailSendRequestedEvent event, String consumerGroupId);
}
