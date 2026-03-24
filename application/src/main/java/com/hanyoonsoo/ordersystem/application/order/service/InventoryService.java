package com.hanyoonsoo.ordersystem.application.order.service;

import com.hanyoonsoo.ordersystem.application.event.idempotency.port.out.ProcessedEventRepository;
import com.hanyoonsoo.ordersystem.application.order.event.OrderCreatedEvent;
import com.hanyoonsoo.ordersystem.application.order.port.in.InventoryServicePort;
import com.hanyoonsoo.ordersystem.application.order.port.out.OrderRepository;
import com.hanyoonsoo.ordersystem.application.product.port.out.InventoryCacheRepository;
import com.hanyoonsoo.ordersystem.application.product.port.out.ProductStockRepository;
import com.hanyoonsoo.ordersystem.common.exception.ErrorCode;
import com.hanyoonsoo.ordersystem.common.exception.base.NotFoundException;
import com.hanyoonsoo.ordersystem.common.lock.DistributedLock;
import com.hanyoonsoo.ordersystem.core.domain.order.entity.Order;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class InventoryService implements InventoryServicePort {

    private final ProcessedEventRepository processedEventRepository;
    private final OrderRepository orderRepository;
    private final InventoryCacheRepository inventoryCacheRepository;
    private final ProductStockRepository productStockRepository;

    @Override
    @DistributedLock(key = "'inventory:product:' + #event.productId")
    public void handleOrderCreated(OrderCreatedEvent event, String consumerGroupId) {
        boolean firstProcessed = processedEventRepository.saveIfAbsent(
                consumerGroupId,
                event.eventId(),
                event.eventType(),
                LocalDateTime.now()
        );
        if (!firstProcessed) {
            return;
        }

        Order order = orderRepository.findById(event.orderId())
                .orElseThrow(() -> new NotFoundException(ErrorCode.ORDER_NOT_FOUND));

        if (!order.isPending()) {
            return;
        }

        Long currentStock = inventoryCacheRepository.findStockByProductId(event.productId())
                .orElseGet(() -> productStockRepository.findStockByProductId(event.productId()).orElse(null));
        if (currentStock == null || currentStock < event.quantity()) {
            order.rejectOutOfStock();
            return;
        }

        boolean decreased = productStockRepository.decreaseStock(event.productId(), event.quantity());
        if (!decreased) {
            order.rejectOutOfStock();
            return;
        }

        long updatedStock = currentStock - event.quantity();
        if (updatedStock <= 0L) {
            inventoryCacheRepository.removeStock(event.productId());
        } else {
            inventoryCacheRepository.saveStock(event.productId(), updatedStock);
        }
        order.confirm();
    }
}
