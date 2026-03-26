package com.hanyoonsoo.ordersystem.application.order.service;

import com.hanyoonsoo.ordersystem.application.event.outbox.model.EventTopicKey;
import com.hanyoonsoo.ordersystem.application.event.outbox.port.in.OutboxRelayServicePort;
import com.hanyoonsoo.ordersystem.application.event.outbox.port.out.EventTopicProvider;
import com.hanyoonsoo.ordersystem.application.order.dto.CreateOrderCommand;
import com.hanyoonsoo.ordersystem.application.order.port.out.OrderRepository;
import com.hanyoonsoo.ordersystem.application.product.port.out.ProductRepository;
import com.hanyoonsoo.ordersystem.application.support.fixture.OrderFixture;
import com.hanyoonsoo.ordersystem.common.exception.base.BadRequestException;
import com.hanyoonsoo.ordersystem.common.exception.base.NotFoundException;
import com.hanyoonsoo.ordersystem.common.utils.ObjectMapperUtils;
import com.hanyoonsoo.ordersystem.core.domain.order.entity.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;
    @Mock
    private ProductRepository productRepository;
    @Mock
    private OutboxRelayServicePort outboxRelayService;
    @Mock
    private ObjectMapperUtils objectMapperUtils;
    @Mock
    private EventTopicProvider eventTopicProvider;
    @InjectMocks
    private OrderService orderService;

    @Test
    void 주문_요청_명령이_유효하지_않으면_예외가_발생한다() {
        // given
        CreateOrderCommand command = new CreateOrderCommand(UUID.randomUUID(), null, 0L);

        // when & then
        assertThatThrownBy(() -> orderService.requestOrder(command))
                .isInstanceOf(BadRequestException.class);
    }

    @Test
    void 주문_요청시_상품이_존재하지_않으면_예외가_발생한다() {
        // given
        CreateOrderCommand command = OrderFixture.주문생성명령();
        given(productRepository.existsById(command.productId())).willReturn(false);

        // when & then
        assertThatThrownBy(() -> orderService.requestOrder(command))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void 주문_요청에_성공하면_주문을_저장하고_아웃박스_이벤트를_추가한다() {
        // given
        CreateOrderCommand command = OrderFixture.주문생성명령();
        given(productRepository.existsById(command.productId())).willReturn(true);
        given(objectMapperUtils.writeValueAsString(any())).willReturn("payload-json");
        given(eventTopicProvider.topicOf(EventTopicKey.ORDER_CREATED)).willReturn("order.created.v1");

        // when
        UUID orderId = orderService.requestOrder(command);

        // then
        ArgumentCaptor<Order> orderCaptor = ArgumentCaptor.forClass(Order.class);
        then(orderRepository).should().save(orderCaptor.capture());
        Order savedOrder = orderCaptor.getValue();
        assertThat(orderId).isEqualTo(savedOrder.getId());
        then(outboxRelayService).should().append(
                eq("order.created.v1"),
                eq("order.created"),
                eq(command.productId().toString()),
                eq("payload-json"),
                any(LocalDateTime.class)
        );
    }

    @Test
    void DLT_이벤트를_처리하면_PENDING_주문을_FAILED로_변경한다() {
        // given
        Order order = OrderFixture.대기중_주문();
        given(orderRepository.findById(order.getId())).willReturn(Optional.of(order));

        // when
        orderService.handleOrderCreatedDltEvent(order.getId());

        // then
        assertThat(order.getStatus()).isEqualTo(com.hanyoonsoo.ordersystem.core.domain.order.entity.OrderStatus.FAILED);
    }

    @Test
    void DLT_이벤트_처리시_PENDING이_아닌_주문은_건너뛴다() {
        // given
        Order order = OrderFixture.대기중_주문();
        order.confirm();
        given(orderRepository.findById(order.getId())).willReturn(Optional.of(order));

        // when
        orderService.handleOrderCreatedDltEvent(order.getId());

        // then
        assertThat(order.getStatus()).isEqualTo(com.hanyoonsoo.ordersystem.core.domain.order.entity.OrderStatus.CONFIRMED);
    }
}
