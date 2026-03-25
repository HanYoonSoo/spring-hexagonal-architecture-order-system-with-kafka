package com.hanyoonsoo.ordersystem.application.idempotency.port.in;

import com.hanyoonsoo.ordersystem.application.idempotency.model.IdempotencyKeyMetadata;

public interface IdemPotencyServicePort {
    boolean saveIfAbsentIdempotencyKey(String idempotencyKey, IdempotencyKeyMetadata metadata);
}
