package com.hanyoonsoo.ordersystem.application.order.port.in;

import com.hanyoonsoo.ordersystem.application.order.event.OrderCreatedEvent;

public interface InventoryServicePort {

    void handleOrderCreated(OrderCreatedEvent event, String consumerGroupId);
}
