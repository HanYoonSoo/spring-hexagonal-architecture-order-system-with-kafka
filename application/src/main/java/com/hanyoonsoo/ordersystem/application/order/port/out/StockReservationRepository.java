package com.hanyoonsoo.ordersystem.application.order.port.out;

public interface StockReservationRepository {

    boolean reserve(Long productId, Long quantity);
}
