package com.hanyoonsoo.ordersystem.application.notification.port.in;

import com.hanyoonsoo.ordersystem.application.order.event.OrderResultEvent;

public interface NotificationServicePort {

    void sendOrderResultNotification(OrderResultEvent event, String consumerGroupId);
}
