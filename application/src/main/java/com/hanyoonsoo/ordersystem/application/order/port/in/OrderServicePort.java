package com.hanyoonsoo.ordersystem.application.order.port.in;

import com.hanyoonsoo.ordersystem.application.order.dto.CreateOrderCommand;

import java.util.UUID;

public interface OrderServicePort {

    UUID requestOrder(CreateOrderCommand command);

    void handleOrderCreatedDltEvent(UUID orderId);
}
