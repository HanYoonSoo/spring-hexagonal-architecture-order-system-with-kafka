package com.hanyoonsoo.ordersystem.adapter.in.kafka.consumer;

import com.hanyoonsoo.ordersystem.adapter.config.kafka.KafkaConsumerErrorHandlerProperties;
import com.hanyoonsoo.ordersystem.application.event.outbox.model.EventTopicKey;
import com.hanyoonsoo.ordersystem.application.event.outbox.port.out.EventTopicProvider;
import com.hanyoonsoo.ordersystem.application.order.port.in.OrderServicePort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.support.Acknowledgment;

import java.util.Map;
import java.util.UUID;

import static org.mockito.Mockito.never;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class DeadLetterEventKafkaConsumerTest {

    @Mock
    private OrderServicePort orderService;
    @Mock
    private EventTopicProvider eventTopicProvider;
    @Mock
    private KafkaConsumerErrorHandlerProperties kafkaConsumerErrorHandlerProperties;
    @Mock
    private Acknowledgment acknowledgment;
    @InjectMocks
    private DeadLetterEventKafkaConsumer consumer;

    @BeforeEach
    void setUp() {
        given(kafkaConsumerErrorHandlerProperties.getDltSuffix()).willReturn(".dlt");
    }

    @Test
    void 원본_토픽을_해석하지_못하면_건너뛰고_acknowledge를_호출한다() {
        // given

        // when
        consumer.handleDeadLetterEvent(Map.of(), acknowledgment, "unknown.dlt", null);

        // then
        then(orderService).should(never()).handleOrderCreatedDltEvent(org.mockito.ArgumentMatchers.any());
        then(acknowledgment).should().acknowledge();
    }

    @Test
    void 주문_생성_DLT를_받으면_주문을_실패_처리한다() {
        // given
        UUID orderId = UUID.randomUUID();
        given(eventTopicProvider.topicOf(EventTopicKey.ORDER_CREATED)).willReturn("order.created.v1");

        // when
        consumer.handleDeadLetterEvent(Map.of("orderId", orderId.toString()), acknowledgment, "order.created.v1.dlt", null);

        // then
        then(orderService).should().handleOrderCreatedDltEvent(orderId);
        then(acknowledgment).should().acknowledge();
    }

    @Test
    void 주문_ID를_파싱하지_못하면_실패_처리하지_않고_acknowledge를_호출한다() {
        // given
        given(eventTopicProvider.topicOf(EventTopicKey.ORDER_CREATED)).willReturn("order.created.v1");

        // when
        consumer.handleDeadLetterEvent(Map.of(), acknowledgment, "order.created.v1.dlt", null);

        // then
        then(orderService).should(never()).handleOrderCreatedDltEvent(org.mockito.ArgumentMatchers.any());
        then(acknowledgment).should().acknowledge();
    }
}
