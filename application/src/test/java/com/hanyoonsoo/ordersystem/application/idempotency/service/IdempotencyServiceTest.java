package com.hanyoonsoo.ordersystem.application.idempotency.service;

import com.hanyoonsoo.ordersystem.application.idempotency.model.IdempotencyKeyMetadata;
import com.hanyoonsoo.ordersystem.application.idempotency.port.out.IdempotencyKeyRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class IdempotencyServiceTest {

    @Mock
    private IdempotencyKeyRepository idempotencyKeyRepository;
    @InjectMocks
    private IdempotencyService idempotencyService;

    @Test
    void 멱등성_키_저장을_저장소에_위임한다() {
        // given
        IdempotencyKeyMetadata metadata = new IdempotencyKeyMetadata("POST", "/api/v1/orders", LocalDateTime.of(2026, 3, 26, 14, 0));
        given(idempotencyKeyRepository.saveIfAbsentIdempotencyKey("idem-key", metadata)).willReturn(true);

        // when
        boolean actual = idempotencyService.saveIfAbsentIdempotencyKey("idem-key", metadata);

        // then
        assertThat(actual).isTrue();
        then(idempotencyKeyRepository).should().saveIfAbsentIdempotencyKey("idem-key", metadata);
    }
}
