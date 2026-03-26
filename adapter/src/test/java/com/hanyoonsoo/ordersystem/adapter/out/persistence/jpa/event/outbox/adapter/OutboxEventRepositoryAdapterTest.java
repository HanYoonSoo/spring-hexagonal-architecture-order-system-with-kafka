package com.hanyoonsoo.ordersystem.adapter.out.persistence.jpa.event.outbox.adapter;

import com.hanyoonsoo.ordersystem.adapter.support.container.IntegrationTestContainerSupporter;
import com.hanyoonsoo.ordersystem.adapter.support.fixture.OutboxEventFixture;
import com.hanyoonsoo.ordersystem.core.domain.event.outbox.entity.OutboxEvent;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class OutboxEventRepositoryAdapterTest extends IntegrationTestContainerSupporter {

    @Autowired
    private OutboxEventRepositoryAdapter outboxEventRepositoryAdapter;

    @Test
    void 아웃박스_이벤트를_저장한다() {
        // given
        OutboxEvent outboxEvent = OutboxEventFixture.대기중_아웃박스_이벤트();

        // when
        outboxEventRepositoryAdapter.save(outboxEvent);
        List<OutboxEvent> actual = outboxEventRepositoryAdapter.findPendingPublishTargets(LocalDateTime.of(2026, 3, 26, 12, 1), 10);

        // then
        assertThat(actual).hasSize(1);
        assertThat(actual.getFirst().getEventType()).isEqualTo("order.created");
    }

    @Test
    void 발행_대상_아웃박스_이벤트를_조회한다() {
        // given
        outboxEventRepositoryAdapter.save(OutboxEventFixture.대기중_아웃박스_이벤트());

        // when
        List<OutboxEvent> actual = outboxEventRepositoryAdapter.findPendingPublishTargets(LocalDateTime.of(2026, 3, 26, 12, 1), 10);

        // then
        assertThat(actual).hasSize(1);
        assertThat(actual.getFirst().getTopic()).isEqualTo("order.created.v1");
    }
}
