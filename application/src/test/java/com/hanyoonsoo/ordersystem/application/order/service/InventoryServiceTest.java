package com.hanyoonsoo.ordersystem.application.order.service;

import com.hanyoonsoo.ordersystem.application.event.idempotency.port.out.ProcessedEventRepository;
import com.hanyoonsoo.ordersystem.application.order.event.OrderCreatedEvent;
import com.hanyoonsoo.ordersystem.application.order.port.out.OrderRepository;
import com.hanyoonsoo.ordersystem.application.product.port.out.InventoryCacheRepository;
import com.hanyoonsoo.ordersystem.application.product.port.out.ProductStockRepository;
import com.hanyoonsoo.ordersystem.application.support.fixture.EventFixture;
import com.hanyoonsoo.ordersystem.application.support.fixture.OrderFixture;
import com.hanyoonsoo.ordersystem.common.exception.base.NotFoundException;
import com.hanyoonsoo.ordersystem.core.domain.order.entity.Order;
import com.hanyoonsoo.ordersystem.core.domain.order.entity.OrderStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class InventoryServiceTest {

    @Mock
    private ProcessedEventRepository processedEventRepository;
    @Mock
    private OrderRepository orderRepository;
    @Mock
    private InventoryCacheRepository inventoryCacheRepository;
    @Mock
    private ProductStockRepository productStockRepository;
    @InjectMocks
    private InventoryService inventoryService;

    @Test
    void 이미_처리된_이벤트면_즉시_반환한다() {
        // given
        OrderCreatedEvent event = EventFixture.주문생성이벤트();
        given(processedEventRepository.saveIfAbsent(anyString(), any(), anyString(), any())).willReturn(false);

        // when
        inventoryService.handleOrderCreated(event, "inventory-order-created-v1");

        // then
        then(orderRepository).should(never()).findById(any());
    }

    @Test
    void 주문이_존재하지_않으면_예외가_발생한다() {
        // given
        OrderCreatedEvent event = EventFixture.주문생성이벤트();
        given(processedEventRepository.saveIfAbsent(anyString(), any(), anyString(), any())).willReturn(true);
        given(orderRepository.findById(event.orderId())).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> inventoryService.handleOrderCreated(event, "inventory-order-created-v1"))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void 주문_상태가_PENDING이_아니면_처리를_건너뛴다() {
        // given
        OrderCreatedEvent event = EventFixture.주문생성이벤트();
        Order order = OrderFixture.대기중_주문();
        order.confirm();
        given(processedEventRepository.saveIfAbsent(anyString(), any(), anyString(), any())).willReturn(true);
        given(orderRepository.findById(event.orderId())).willReturn(Optional.of(order));

        // when
        inventoryService.handleOrderCreated(event, "inventory-order-created-v1");

        // then
        then(productStockRepository).should(never()).decreaseStock(any(), any());
    }

    @Test
    void 재고_정보가_없으면_주문을_거절한다() {
        // given
        OrderCreatedEvent event = EventFixture.주문생성이벤트();
        Order order = OrderFixture.대기중_주문(event.userId(), event.productId(), event.quantity());
        given(processedEventRepository.saveIfAbsent(anyString(), any(), anyString(), any())).willReturn(true);
        given(orderRepository.findById(event.orderId())).willReturn(Optional.of(order));
        given(inventoryCacheRepository.findStockByProductId(event.productId())).willReturn(Optional.empty());
        given(productStockRepository.findStockByProductId(event.productId())).willReturn(Optional.empty());

        // when
        inventoryService.handleOrderCreated(event, "inventory-order-created-v1");

        // then
        assertThat(order.getStatus()).isEqualTo(OrderStatus.REJECTED_OUT_OF_STOCK);
    }

    @Test
    void 재고가_부족하면_주문을_거절한다() {
        // given
        OrderCreatedEvent event = EventFixture.주문생성이벤트();
        Order order = OrderFixture.대기중_주문(event.userId(), event.productId(), event.quantity());
        given(processedEventRepository.saveIfAbsent(anyString(), any(), anyString(), any())).willReturn(true);
        given(orderRepository.findById(event.orderId())).willReturn(Optional.of(order));
        given(inventoryCacheRepository.findStockByProductId(event.productId())).willReturn(Optional.of(1L));

        // when
        inventoryService.handleOrderCreated(event, "inventory-order-created-v1");

        // then
        assertThat(order.getStatus()).isEqualTo(OrderStatus.REJECTED_OUT_OF_STOCK);
    }

    @Test
    void 재고_차감에_실패하면_주문을_거절한다() {
        // given
        OrderCreatedEvent event = EventFixture.주문생성이벤트();
        Order order = OrderFixture.대기중_주문(event.userId(), event.productId(), event.quantity());
        given(processedEventRepository.saveIfAbsent(anyString(), any(), anyString(), any())).willReturn(true);
        given(orderRepository.findById(event.orderId())).willReturn(Optional.of(order));
        given(inventoryCacheRepository.findStockByProductId(event.productId())).willReturn(Optional.of(10L));
        given(productStockRepository.decreaseStock(event.productId(), event.quantity())).willReturn(false);

        // when
        inventoryService.handleOrderCreated(event, "inventory-order-created-v1");

        // then
        assertThat(order.getStatus()).isEqualTo(OrderStatus.REJECTED_OUT_OF_STOCK);
    }

    @Test
    void 차감_후_재고가_0이하이면_캐시를_삭제하고_주문을_확정한다() {
        // given
        OrderCreatedEvent event = EventFixture.주문생성이벤트();
        Order order = OrderFixture.대기중_주문(event.userId(), event.productId(), event.quantity());
        given(processedEventRepository.saveIfAbsent(anyString(), any(), anyString(), any())).willReturn(true);
        given(orderRepository.findById(event.orderId())).willReturn(Optional.of(order));
        given(inventoryCacheRepository.findStockByProductId(event.productId())).willReturn(Optional.of(2L));
        given(productStockRepository.decreaseStock(event.productId(), event.quantity())).willReturn(true);

        // when
        inventoryService.handleOrderCreated(event, "inventory-order-created-v1");

        // then
        then(inventoryCacheRepository).should().removeStock(event.productId());
        assertThat(order.getStatus()).isEqualTo(OrderStatus.CONFIRMED);
    }

    @Test
    void 차감_후_재고가_남아있으면_캐시를_갱신하고_주문을_확정한다() {
        // given
        OrderCreatedEvent event = EventFixture.주문생성이벤트();
        Order order = OrderFixture.대기중_주문(event.userId(), event.productId(), event.quantity());
        given(processedEventRepository.saveIfAbsent(anyString(), any(), anyString(), any())).willReturn(true);
        given(orderRepository.findById(event.orderId())).willReturn(Optional.of(order));
        given(inventoryCacheRepository.findStockByProductId(event.productId())).willReturn(Optional.of(10L));
        given(productStockRepository.decreaseStock(event.productId(), event.quantity())).willReturn(true);

        // when
        inventoryService.handleOrderCreated(event, "inventory-order-created-v1");

        // then
        then(inventoryCacheRepository).should().saveStock(event.productId(), 8L);
        assertThat(order.getStatus()).isEqualTo(OrderStatus.CONFIRMED);
    }
}
