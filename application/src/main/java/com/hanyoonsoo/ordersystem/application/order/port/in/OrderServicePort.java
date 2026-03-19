package com.hanyoonsoo.ordersystem.application.order.port.in;

import com.hanyoonsoo.ordersystem.application.order.dto.CreateOrderCommand;
import com.hanyoonsoo.ordersystem.application.order.event.OrderCreatedEvent;

import java.util.UUID;

public interface OrderServicePort {

    UUID requestOrder(CreateOrderCommand command);

    void handleOrderCreatedEvent(OrderCreatedEvent event);

    void handleOrderCreatedDltEvent(UUID orderId);
}
