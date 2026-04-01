package com.hanyoonsoo.ordersystem.application.order.service;

import com.hanyoonsoo.ordersystem.application.event.idempotency.port.out.ProcessedEventRepository;
import com.hanyoonsoo.ordersystem.application.event.outbox.model.EventTopicKey;
import com.hanyoonsoo.ordersystem.application.event.outbox.port.in.OutboxRelayServicePort;
import com.hanyoonsoo.ordersystem.application.event.outbox.port.out.EventTopicProvider;
import com.hanyoonsoo.ordersystem.application.order.event.OrderCreatedEvent;
import com.hanyoonsoo.ordersystem.application.order.event.OrderEventType;
import com.hanyoonsoo.ordersystem.application.order.event.OrderResultEvent;
import com.hanyoonsoo.ordersystem.application.order.port.in.InventoryServicePort;
import com.hanyoonsoo.ordersystem.application.order.port.out.OrderRepository;
import com.hanyoonsoo.ordersystem.application.product.port.out.InventoryCacheRepository;
import com.hanyoonsoo.ordersystem.application.product.port.out.ProductStockRepository;
import com.hanyoonsoo.ordersystem.common.exception.ErrorCode;
import com.hanyoonsoo.ordersystem.common.exception.base.NotFoundException;
import com.hanyoonsoo.ordersystem.common.lock.DistributedLock;
import com.hanyoonsoo.ordersystem.common.utils.ObjectMapperUtils;
import com.hanyoonsoo.ordersystem.core.domain.order.entity.Order;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class InventoryService implements InventoryServicePort {

    private final ProcessedEventRepository processedEventRepository;
    private final OrderRepository orderRepository;
    private final InventoryCacheRepository inventoryCacheRepository;
    private final ProductStockRepository productStockRepository;
    private final OutboxRelayServicePort outboxRelayService;
    private final EventTopicProvider eventTopicProvider;
    private final ObjectMapperUtils objectMapperUtils;

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
            appendOrderResultEvent(order);
            return;
        }

        boolean decreased = productStockRepository.decreaseStock(event.productId(), event.quantity());
        if (!decreased) {
            order.rejectOutOfStock();
            appendOrderResultEvent(order);
            return;
        }

        long updatedStock = currentStock - event.quantity();
        if (updatedStock <= 0L) {
            inventoryCacheRepository.removeStock(event.productId());
        } else {
            inventoryCacheRepository.saveStock(event.productId(), updatedStock);
        }
        order.confirm();
        appendOrderResultEvent(order);
    }

    private void appendOrderResultEvent(Order order) {
        OrderResultEvent event = new OrderResultEvent(
                UUID.randomUUID(),
                OrderEventType.ORDER_RESULT.value(),
                LocalDateTime.now(),
                order.getId(),
                order.getUserId(),
                order.getProductId(),
                order.getQuantity(),
                order.getStatus()
        );
        outboxRelayService.append(
                eventTopicProvider.topicOf(EventTopicKey.ORDER_RESULT),
                event.eventType(),
                order.getId().toString(),
                objectMapperUtils.writeValueAsString(event),
                event.occurredAt()
        );
    }
}
