package com.hanyoonsoo.ordersystem.core.domain.event.idempotency.entity;

import com.hanyoonsoo.ordersystem.core.support.fixture.ProcessedEventFixture;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ProcessedEventTest {

    @Test
    void 생성하면_ID가_자동으로_부여된_처리_이력을_반환한다() {
        // given
        ProcessedEvent processedEvent = ProcessedEventFixture.처리된_이벤트();

        // when
        String eventType = processedEvent.getEventType();

        // then
        assertThat(processedEvent.getId()).isNotNull();
        assertThat(processedEvent.getConsumerGroupId()).isEqualTo("inventory-order-created-v1");
        assertThat(eventType).isEqualTo("order.created");
        assertThat(processedEvent.getProcessedAt()).isNotNull();
    }
}
