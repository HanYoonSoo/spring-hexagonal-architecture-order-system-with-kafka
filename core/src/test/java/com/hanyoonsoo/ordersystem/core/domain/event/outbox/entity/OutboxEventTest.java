package com.hanyoonsoo.ordersystem.core.domain.event.outbox.entity;

import com.hanyoonsoo.ordersystem.core.support.fixture.OutboxEventFixture;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class OutboxEventTest {

    @Test
    void 대기중_아웃박스_이벤트는_PENDING_상태로_시작한다() {
        // given
        OutboxEvent outboxEvent = OutboxEventFixture.대기중_아웃박스이벤트();

        // when
        OutboxStatus actual = outboxEvent.getStatus();

        // then
        assertThat(actual).isEqualTo(OutboxStatus.PENDING);
        assertThat(outboxEvent.getRetryCount()).isZero();
        assertThat(outboxEvent.getId()).isNotNull();
    }

    @Test
    void 발행_완료_처리하면_상태와_발행시각이_변경된다() {
        // given
        OutboxEvent outboxEvent = OutboxEventFixture.대기중_아웃박스이벤트();
        LocalDateTime publishedAt = LocalDateTime.of(2026, 3, 26, 12, 1);

        // when
        outboxEvent.markPublished(publishedAt);

        // then
        assertThat(outboxEvent.getStatus()).isEqualTo(OutboxStatus.PUBLISHED);
        assertThat(outboxEvent.getPublishedAt()).isEqualTo(publishedAt);
        assertThat(outboxEvent.getLastError()).isNull();
    }

    @Test
    void 최대_재시도_이전에는_재시도_처리해도_PENDING_상태를_유지한다() {
        // given
        OutboxEvent outboxEvent = OutboxEventFixture.대기중_아웃박스이벤트();
        LocalDateTime nextRetryAt = LocalDateTime.of(2026, 3, 26, 12, 2);

        // when
        outboxEvent.markRetry(nextRetryAt, "temporary failure", 5);

        // then
        assertThat(outboxEvent.getStatus()).isEqualTo(OutboxStatus.PENDING);
        assertThat(outboxEvent.getRetryCount()).isEqualTo(1);
        assertThat(outboxEvent.getNextRetryAt()).isEqualTo(nextRetryAt);
        assertThat(outboxEvent.getLastError()).isEqualTo("temporary failure");
    }

    @Test
    void 최대_재시도에_도달하면_FAILED_상태로_변경된다() {
        // given
        OutboxEvent outboxEvent = OutboxEventFixture.대기중_아웃박스이벤트();

        // when
        outboxEvent.markRetry(LocalDateTime.now(), "fatal", 1);

        // then
        assertThat(outboxEvent.getStatus()).isEqualTo(OutboxStatus.FAILED);
        assertThat(outboxEvent.getRetryCount()).isEqualTo(1);
    }

    @Test
    void 재시도_실패_메시지가_길면_허용_길이로_잘라낸다() {
        // given
        OutboxEvent outboxEvent = OutboxEventFixture.대기중_아웃박스이벤트();
        String longMessage = "x".repeat(1205);

        // when
        outboxEvent.markRetry(LocalDateTime.now(), longMessage, 5);

        // then
        assertThat(outboxEvent.getLastError()).hasSize(1000);
    }
}
