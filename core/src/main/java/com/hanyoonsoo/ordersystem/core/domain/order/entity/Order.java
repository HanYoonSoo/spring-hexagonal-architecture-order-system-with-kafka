package com.hanyoonsoo.ordersystem.core.domain.order.entity;

import com.hanyoonsoo.ordersystem.core.domain.common.SoftDeleteTimeEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@Entity
@Table(name = "orders")
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
public class Order extends SoftDeleteTimeEntity {

    @Id
    @Column(name = "id", nullable = false)
    private UUID id;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "product_id", nullable = false)
    private Long productId;

    @Column(name = "quantity", nullable = false)
    private Long quantity;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private OrderStatus status;

    private Order(UUID userId, Long productId, Long quantity) {
        this.id = UUID.randomUUID();
        this.userId = userId;
        this.productId = productId;
        this.quantity = quantity;
        this.status = OrderStatus.PENDING;
    }

    public static Order pending(UUID userId, Long productId, Long quantity) {
        return new Order(userId, productId, quantity);
    }

    public void confirm() {
        this.status = OrderStatus.CONFIRMED;
    }

    public void rejectOutOfStock() {
        this.status = OrderStatus.REJECTED_OUT_OF_STOCK;
    }

    public boolean isPending() {
        return this.status == OrderStatus.PENDING;
    }
}
