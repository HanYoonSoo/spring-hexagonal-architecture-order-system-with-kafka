package com.hanyoonsoo.ordersystem.application.idempotency.service;

import com.hanyoonsoo.ordersystem.application.idempotency.model.IdempotencyKeyMetadata;
import com.hanyoonsoo.ordersystem.application.idempotency.port.in.IdemPotencyServicePort;
import com.hanyoonsoo.ordersystem.application.idempotency.port.out.IdemPotencyKeyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class IdemPotencyService implements IdemPotencyServicePort {

    private final IdemPotencyKeyRepository idemPotencyKeyRepository;

    @Override
    public boolean saveIfAbsentIdempotencyKey(String idempotencyKey, IdempotencyKeyMetadata metadata) {
        return idemPotencyKeyRepository.saveIfAbsentIdempotencyKey(idempotencyKey, metadata);
    }
}
