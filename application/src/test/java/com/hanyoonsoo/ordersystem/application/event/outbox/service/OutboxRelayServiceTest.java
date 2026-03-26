package com.hanyoonsoo.ordersystem.application.event.outbox.service;

import com.hanyoonsoo.ordersystem.application.event.outbox.config.OutboxRelayProperties;
import com.hanyoonsoo.ordersystem.application.event.outbox.port.out.OutboxEventPublisher;
import com.hanyoonsoo.ordersystem.application.event.outbox.port.out.OutboxEventRepository;
import com.hanyoonsoo.ordersystem.application.support.fixture.EventFixture;
import com.hanyoonsoo.ordersystem.core.domain.event.outbox.entity.OutboxEvent;
import com.hanyoonsoo.ordersystem.core.domain.event.outbox.entity.OutboxStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.never;

@ExtendWith(MockitoExtension.class)
class OutboxRelayServiceTest {

    @Mock
    private OutboxEventRepository outboxEventRepository;
    @Mock
    private OutboxEventPublisher outboxEventPublisher;
    private OutboxRelayProperties outboxRelayProperties;
    private OutboxRelayService outboxRelayService;

    @BeforeEach
    void setUp() {
        outboxRelayProperties = new OutboxRelayProperties();
        outboxRelayService = new OutboxRelayService(outboxEventRepository, outboxEventPublisher, outboxRelayProperties);
    }

    @Test
    void 아웃박스_이벤트를_추가하면_PENDING_상태의_이벤트를_생성한다() {
        // given
        LocalDateTime occurredAt = LocalDateTime.of(2026, 3, 26, 12, 0);

        // when
        outboxRelayService.append("order.created.v1", "order.created", "1", "payload", occurredAt);

        // then
        ArgumentCaptor<OutboxEvent> outboxCaptor = ArgumentCaptor.forClass(OutboxEvent.class);
        then(outboxEventRepository).should().save(outboxCaptor.capture());
        assertThat(outboxCaptor.getValue().getStatus()).isEqualTo(OutboxStatus.PENDING);
        assertThat(outboxCaptor.getValue().getOccurredAt()).isEqualTo(occurredAt);
    }

    @Test
    void 릴레이가_비활성화되어_있으면_즉시_반환한다() {
        // given
        outboxRelayProperties.setEnabled(false);

        // when
        outboxRelayService.relay();

        // then
        then(outboxEventRepository).should(never()).findPendingPublishTargets(any(), any(Integer.class));
    }

    @Test
    void 발행_대상이_없으면_즉시_반환한다() {
        // given
        given(outboxEventRepository.findPendingPublishTargets(any(), eq(outboxRelayProperties.getBatchSize())))
                .willReturn(List.of());

        // when
        outboxRelayService.relay();

        // then
        then(outboxEventPublisher).should(never()).publish(any(), any(), any());
    }

    @Test
    void 릴레이에_성공하면_이벤트를_발행하고_PUBLISHED_상태로_변경한다() {
        // given
        OutboxEvent outboxEvent = EventFixture.아웃박스이벤트();
        given(outboxEventRepository.findPendingPublishTargets(any(), eq(outboxRelayProperties.getBatchSize())))
                .willReturn(List.of(outboxEvent));

        // when
        outboxRelayService.relay();

        // then
        then(outboxEventPublisher).should().publish(outboxEvent.getTopic(), outboxEvent.getEventKey(), outboxEvent.getPayload());
        assertThat(outboxEvent.getStatus()).isEqualTo(OutboxStatus.PUBLISHED);
    }

    @Test
    void 릴레이에_실패하면_재시도_상태로_표시한다() {
        // given
        OutboxEvent outboxEvent = EventFixture.아웃박스이벤트();
        given(outboxEventRepository.findPendingPublishTargets(any(), eq(outboxRelayProperties.getBatchSize())))
                .willReturn(List.of(outboxEvent));
        willThrow(new RuntimeException("publish failed")).given(outboxEventPublisher)
                .publish(outboxEvent.getTopic(), outboxEvent.getEventKey(), outboxEvent.getPayload());

        // when
        outboxRelayService.relay();

        // then
        assertThat(outboxEvent.getStatus()).isEqualTo(OutboxStatus.PENDING);
        assertThat(outboxEvent.getRetryCount()).isEqualTo(1);
        assertThat(outboxEvent.getLastError()).isEqualTo("publish failed");
    }
}
