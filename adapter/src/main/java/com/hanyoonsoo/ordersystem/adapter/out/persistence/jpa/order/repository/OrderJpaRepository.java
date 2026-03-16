package com.hanyoonsoo.ordersystem.adapter.out.persistence.jpa.order.repository;

import com.hanyoonsoo.ordersystem.core.domain.order.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface OrderJpaRepository extends JpaRepository<Order, UUID> {
}
