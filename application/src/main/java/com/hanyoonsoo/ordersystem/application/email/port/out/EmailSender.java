package com.hanyoonsoo.ordersystem.application.email.port.out;

import com.hanyoonsoo.ordersystem.application.email.event.EmailSendRequestedEvent;

public interface EmailSender {

    void sendOrderResultEmail(String email, EmailSendRequestedEvent event);
}
