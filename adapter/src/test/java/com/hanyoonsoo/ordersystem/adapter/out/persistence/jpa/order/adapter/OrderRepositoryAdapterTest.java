package com.hanyoonsoo.ordersystem.adapter.out.persistence.jpa.order.adapter;

import com.hanyoonsoo.ordersystem.adapter.support.container.IntegrationTestContainerSupporter;
import com.hanyoonsoo.ordersystem.adapter.support.fixture.OrderFixture;
import com.hanyoonsoo.ordersystem.core.domain.order.entity.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class OrderRepositoryAdapterTest extends IntegrationTestContainerSupporter {

    @Autowired
    private OrderRepositoryAdapter orderRepositoryAdapter;

    @Test
    void 주문을_저장한다() {
        // given
        Order order = OrderFixture.대기중_주문();

        // when
        Order actual = orderRepositoryAdapter.save(order);

        // then
        assertThat(actual.getId()).isNotNull();
        assertThat(actual.getStatus()).isEqualTo(order.getStatus());
    }

    @Test
    void 주문_ID로_주문을_조회한다() {
        // given
        Order savedOrder = orderRepositoryAdapter.save(OrderFixture.대기중_주문());

        // when
        Optional<Order> actual = orderRepositoryAdapter.findById(savedOrder.getId());

        // then
        assertThat(actual).isPresent();
        assertThat(actual.get().getId()).isEqualTo(savedOrder.getId());
    }
}
