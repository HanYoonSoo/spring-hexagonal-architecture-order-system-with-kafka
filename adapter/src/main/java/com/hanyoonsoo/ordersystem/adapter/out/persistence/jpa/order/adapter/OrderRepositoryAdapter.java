package com.hanyoonsoo.ordersystem.adapter.out.persistence.jpa.order.adapter;

import com.hanyoonsoo.ordersystem.adapter.out.persistence.jpa.order.repository.OrderJpaRepository;
import com.hanyoonsoo.ordersystem.application.order.port.out.OrderRepository;
import com.hanyoonsoo.ordersystem.core.domain.order.entity.Order;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class OrderRepositoryAdapter implements OrderRepository {

    private final OrderJpaRepository orderJpaRepository;

    @Override
    public Order save(Order order) {
        return orderJpaRepository.save(order);
    }

    @Override
    public Optional<Order> findById(UUID orderId) {
        return orderJpaRepository.findById(orderId);
    }
}
