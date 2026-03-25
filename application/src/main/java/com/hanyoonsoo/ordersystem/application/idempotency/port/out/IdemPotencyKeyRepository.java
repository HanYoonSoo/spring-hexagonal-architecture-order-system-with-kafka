package com.hanyoonsoo.ordersystem.application.idempotency.port.out;

import com.hanyoonsoo.ordersystem.application.idempotency.model.IdempotencyKeyMetadata;

public interface IdemPotencyKeyRepository {
    boolean saveIfAbsentIdempotencyKey(String idempotencyKey, IdempotencyKeyMetadata metadata);
}
