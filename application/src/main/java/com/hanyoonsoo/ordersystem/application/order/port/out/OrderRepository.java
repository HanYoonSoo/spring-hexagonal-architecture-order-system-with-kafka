package com.hanyoonsoo.ordersystem.application.order.port.out;

import com.hanyoonsoo.ordersystem.core.domain.order.entity.Order;

import java.util.Optional;
import java.util.UUID;

public interface OrderRepository {

    Order save(Order order);

    Optional<Order> findById(UUID orderId);
}
