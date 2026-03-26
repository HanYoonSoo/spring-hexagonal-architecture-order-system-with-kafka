package com.hanyoonsoo.ordersystem.adapter.out.persistence.jpa.event.idempotency.adapter;

import com.hanyoonsoo.ordersystem.adapter.support.container.IntegrationTestContainerSupporter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class ProcessedEventRepositoryAdapterTest extends IntegrationTestContainerSupporter {

    @Autowired
    private ProcessedEventRepositoryAdapter processedEventRepositoryAdapter;

    @Test
    void 처음_이벤트는_저장에_성공하고_중복_이벤트는_저장에_실패한다() {
        // given
        UUID eventId = UUID.randomUUID();
        LocalDateTime processedAt = LocalDateTime.of(2026, 3, 26, 12, 0);

        // when
        boolean firstSaved = processedEventRepositoryAdapter.saveIfAbsent(
                "inventory-order-created-v1",
                eventId,
                "order.created",
                processedAt
        );
        boolean duplicatedSaved = processedEventRepositoryAdapter.saveIfAbsent(
                "inventory-order-created-v1",
                eventId,
                "order.created",
                processedAt
        );

        // then
        assertThat(firstSaved).isTrue();
        assertThat(duplicatedSaved).isFalse();
    }
}
