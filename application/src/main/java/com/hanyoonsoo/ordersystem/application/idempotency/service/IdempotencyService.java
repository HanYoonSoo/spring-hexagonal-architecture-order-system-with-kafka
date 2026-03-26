package com.hanyoonsoo.ordersystem.application.idempotency.service;

import com.hanyoonsoo.ordersystem.application.idempotency.model.IdempotencyKeyMetadata;
import com.hanyoonsoo.ordersystem.application.idempotency.port.in.IdempotencyServicePort;
import com.hanyoonsoo.ordersystem.application.idempotency.port.out.IdempotencyKeyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class IdempotencyService implements IdempotencyServicePort {

    private final IdempotencyKeyRepository idempotencyKeyRepository;

    @Override
    public boolean saveIfAbsentIdempotencyKey(String idempotencyKey, IdempotencyKeyMetadata metadata) {
        return idempotencyKeyRepository.saveIfAbsentIdempotencyKey(idempotencyKey, metadata);
    }
}
