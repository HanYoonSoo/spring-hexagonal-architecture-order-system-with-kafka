package com.hanyoonsoo.ordersystem.application.product.port.out;

import java.util.Optional;

public interface ProductStockRepository {

    Optional<Long> findStockByProductId(Long productId);

    boolean decreaseStock(Long productId, Long quantity);
}
