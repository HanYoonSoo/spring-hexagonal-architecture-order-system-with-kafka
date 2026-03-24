package com.hanyoonsoo.ordersystem.application.order.service;

import com.hanyoonsoo.ordersystem.application.order.dto.CreateOrderCommand;
import com.hanyoonsoo.ordersystem.application.order.event.OrderCreatedEvent;
import com.hanyoonsoo.ordersystem.application.order.event.OrderEventType;
import com.hanyoonsoo.ordersystem.application.order.port.in.OrderServicePort;
import com.hanyoonsoo.ordersystem.application.order.port.out.OrderRepository;
import com.hanyoonsoo.ordersystem.application.event.outbox.model.EventTopicKey;
import com.hanyoonsoo.ordersystem.application.event.outbox.port.in.OutboxRelayServicePort;
import com.hanyoonsoo.ordersystem.application.event.outbox.port.out.EventTopicProvider;
import com.hanyoonsoo.ordersystem.common.exception.ErrorCode;
import com.hanyoonsoo.ordersystem.common.exception.base.BadRequestException;
import com.hanyoonsoo.ordersystem.common.exception.base.NotFoundException;
import com.hanyoonsoo.ordersystem.common.utils.ObjectMapperUtils;
import com.hanyoonsoo.ordersystem.application.product.port.out.ProductRepository;
import com.hanyoonsoo.ordersystem.core.domain.order.entity.Order;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderService implements OrderServicePort {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final OutboxRelayServicePort outboxRelayService;
    private final ObjectMapperUtils objectMapperUtils;
    private final EventTopicProvider eventTopicProvider;

    @Override
    @Transactional
    public UUID requestOrder(CreateOrderCommand command) {
        validate(command);
        if (!productRepository.existsById(command.productId())) {
            throw new NotFoundException(ErrorCode.PRODUCT_NOT_FOUND);
        }

        Order order = Order.pending(command.userId(), command.productId(), command.quantity());
        orderRepository.save(order);

        OrderCreatedEvent event = new OrderCreatedEvent(
                UUID.randomUUID(),
                OrderEventType.ORDER_CREATED.value(),
                LocalDateTime.now(),
                order.getId(),
                command.userId(),
                command.productId(),
                command.quantity()
        );
        outboxRelayService.append(
                eventTopicProvider.topicOf(EventTopicKey.ORDER_CREATED),
                event.eventType(),
                command.productId().toString(),
                objectMapperUtils.writeValueAsString(event),
                event.occurredAt()
        );

        return order.getId();
    }

    @Override
    @Transactional
    public void handleOrderCreatedDltEvent(UUID orderId) {
        orderRepository.findById(orderId).ifPresent(order -> {
            if (!order.isPending()) {
                return;
            }
            order.fail();
        });
    }

    private void validate(CreateOrderCommand command) {
        if (command.productId() == null || command.quantity() == null || command.quantity() <= 0L) {
            throw new BadRequestException(ErrorCode.INVALID_REQUEST);
        }
    }
}
