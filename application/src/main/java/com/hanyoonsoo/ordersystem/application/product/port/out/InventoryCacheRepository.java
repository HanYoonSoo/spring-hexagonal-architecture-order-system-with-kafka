package com.hanyoonsoo.ordersystem.application.product.port.out;

import java.util.Optional;

public interface InventoryCacheRepository {

    Optional<Long> findStockByProductId(Long productId);

    void saveStock(Long productId, Long stock);

    void removeStock(Long productId);
}
