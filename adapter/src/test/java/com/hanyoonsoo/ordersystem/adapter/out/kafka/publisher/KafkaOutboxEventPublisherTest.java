package com.hanyoonsoo.ordersystem.adapter.out.kafka.publisher;

import com.hanyoonsoo.ordersystem.application.event.outbox.exception.OutboxPublishException;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.TopicPartition;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class KafkaOutboxEventPublisherTest {

    @Mock
    private KafkaTemplate<String, String> kafkaTemplate;

    @Test
    void 카프카_발행에_성공한다() throws Exception {
        // given
        KafkaOutboxEventPublisher publisher = new KafkaOutboxEventPublisher(kafkaTemplate);
        RecordMetadata metadata = new RecordMetadata(new TopicPartition("order.created.v1", 0), 0, 3, 0L, 0L, 0, 0);
        CompletableFuture<SendResult<String, String>> future = CompletableFuture.completedFuture(new SendResult<>(null, metadata));
        given(kafkaTemplate.send("order.created.v1", "1", "payload")).willReturn(future);

        // when
        publisher.publish("order.created.v1", "1", "payload");

        // then
    }

    @Test
    void 인터럽트가_발생하면_예외로_변환한다() {
        // given
        KafkaOutboxEventPublisher publisher = new KafkaOutboxEventPublisher(kafkaTemplate);
        CompletableFuture<SendResult<String, String>> future = new CompletableFuture<>() {
            @Override
            public SendResult<String, String> get() throws InterruptedException {
                throw new InterruptedException("interrupted");
            }
        };
        given(kafkaTemplate.send("order.created.v1", "1", "payload")).willReturn(future);

        // when & then
        assertThatThrownBy(() -> publisher.publish("order.created.v1", "1", "payload"))
                .isInstanceOf(OutboxPublishException.class);
    }

    @Test
    void 실행_예외가_발생하면_예외로_변환한다() {
        // given
        KafkaOutboxEventPublisher publisher = new KafkaOutboxEventPublisher(kafkaTemplate);
        CompletableFuture<SendResult<String, String>> future = new CompletableFuture<>() {
            @Override
            public SendResult<String, String> get() throws ExecutionException {
                throw new ExecutionException(new RuntimeException("boom"));
            }
        };
        given(kafkaTemplate.send("order.created.v1", "1", "payload")).willReturn(future);

        // when & then
        assertThatThrownBy(() -> publisher.publish("order.created.v1", "1", "payload"))
                .isInstanceOf(OutboxPublishException.class);
    }
}
