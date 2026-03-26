package com.hanyoonsoo.ordersystem.application.idempotency.port.in;

import com.hanyoonsoo.ordersystem.application.idempotency.model.IdempotencyKeyMetadata;

public interface IdempotencyServicePort {
    boolean saveIfAbsentIdempotencyKey(String idempotencyKey, IdempotencyKeyMetadata metadata);
}
