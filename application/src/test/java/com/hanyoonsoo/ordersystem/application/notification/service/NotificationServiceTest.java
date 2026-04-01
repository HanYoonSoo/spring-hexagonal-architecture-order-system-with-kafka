package com.hanyoonsoo.ordersystem.application.notification.service;

import com.hanyoonsoo.ordersystem.application.event.idempotency.port.out.ProcessedEventRepository;
import com.hanyoonsoo.ordersystem.application.event.outbox.model.EventTopicKey;
import com.hanyoonsoo.ordersystem.application.event.outbox.port.in.OutboxRelayServicePort;
import com.hanyoonsoo.ordersystem.application.event.outbox.port.out.EventTopicProvider;
import com.hanyoonsoo.ordersystem.application.order.event.OrderResultEvent;
import com.hanyoonsoo.ordersystem.application.support.fixture.EventFixture;
import com.hanyoonsoo.ordersystem.application.user.port.out.UserRepository;
import com.hanyoonsoo.ordersystem.core.domain.user.entity.User;
import com.hanyoonsoo.ordersystem.core.domain.user.entity.CredentialProvider;
import com.hanyoonsoo.ordersystem.core.domain.user.entity.UserCredential;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;

@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {

    @Mock
    private ProcessedEventRepository processedEventRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private OutboxRelayServicePort outboxRelayService;
    @Mock
    private EventTopicProvider eventTopicProvider;
    @Mock
    private com.hanyoonsoo.ordersystem.common.utils.ObjectMapperUtils objectMapperUtils;

    @InjectMocks
    private NotificationService notificationService;

    @Test
    void 주문결과_알림을_처음_처리하면_이메일발송요청_아웃박스를_추가한다() {
        // given
        OrderResultEvent event = EventFixture.주문결과이벤트();
        User user = User.from("테스트 사용자");
        UserCredential userCredential = UserCredential.of(
                "user@example.com",
                "encoded-password",
                CredentialProvider.LOCAL,
                user
        );
        given(processedEventRepository.saveIfAbsent(anyString(), any(), anyString(), any())).willReturn(true);
        given(userRepository.findUserCredentialByUserIdAndDeletedAtIsNull(event.userId())).willReturn(Optional.of(userCredential));
        given(eventTopicProvider.topicOf(EventTopicKey.EMAIL_SEND_REQUESTED)).willReturn("email.send.requested.v1");
        given(objectMapperUtils.writeValueAsString(any())).willReturn("payload-json");

        // when
        notificationService.sendOrderResultNotification(event, "notification-order-result-consumer-v1");

        // then
        then(outboxRelayService).should().append(
                eq("email.send.requested.v1"),
                eq("email.send.requested"),
                eq(event.orderId().toString()),
                eq("payload-json"),
                any()
        );
    }

    @Test
    void 이미_처리된_이벤트면_이메일발송요청을_추가하지_않는다() {
        // given
        OrderResultEvent event = EventFixture.주문결과이벤트();
        given(processedEventRepository.saveIfAbsent(anyString(), any(), anyString(), any())).willReturn(false);

        // when
        notificationService.sendOrderResultNotification(event, "notification-order-result-consumer-v1");

        // then
        then(userRepository).should(never()).findUserCredentialByUserIdAndDeletedAtIsNull(any());
        then(outboxRelayService).should(never()).append(anyString(), anyString(), anyString(), anyString(), any());
    }
}
