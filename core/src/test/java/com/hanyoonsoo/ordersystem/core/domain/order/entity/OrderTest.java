package com.hanyoonsoo.ordersystem.core.domain.order.entity;

import com.hanyoonsoo.ordersystem.core.support.fixture.OrderFixture;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class OrderTest {

    @Test
    void 대기중_주문은_PENDING_상태로_시작한다() {
        // given
        Order order = OrderFixture.대기중_주문();

        // when
        OrderStatus actual = order.getStatus();

        // then
        assertThat(actual).isEqualTo(OrderStatus.PENDING);
        assertThat(order.isPending()).isTrue();
        assertThat(order.getId()).isNotNull();
    }

    @Test
    void 주문을_확정하면_CONFIRMED_상태로_변경된다() {
        // given
        Order order = OrderFixture.대기중_주문();

        // when
        order.confirm();

        // then
        assertThat(order.getStatus()).isEqualTo(OrderStatus.CONFIRMED);
        assertThat(order.isPending()).isFalse();
    }

    @Test
    void 재고_부족으로_거절하면_REJECTED_OUT_OF_STOCK_상태로_변경된다() {
        // given
        Order order = OrderFixture.대기중_주문();

        // when
        order.rejectOutOfStock();

        // then
        assertThat(order.getStatus()).isEqualTo(OrderStatus.REJECTED_OUT_OF_STOCK);
    }

    @Test
    void 주문을_실패_처리하면_FAILED_상태로_변경된다() {
        // given
        Order order = OrderFixture.대기중_주문();

        // when
        order.fail();

        // then
        assertThat(order.getStatus()).isEqualTo(OrderStatus.FAILED);
    }
}
