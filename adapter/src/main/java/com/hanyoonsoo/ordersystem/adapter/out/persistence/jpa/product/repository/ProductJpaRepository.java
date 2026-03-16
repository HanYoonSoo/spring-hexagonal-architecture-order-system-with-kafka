package com.hanyoonsoo.ordersystem.adapter.out.persistence.jpa.product.repository;

import com.hanyoonsoo.ordersystem.core.domain.product.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ProductJpaRepository extends JpaRepository<Product, Long> {

    Optional<Product> findProductById(Long productId);

    boolean existsProductById(Long productId);

    @Query("select p.stock from Product p where p.id = :productId")
    Optional<Long> findProductStockByProductId(@Param("productId") Long productId);

    @Modifying
    @Query("""
            update Product p
            set p.stock = p.stock - :quantity
            where p.id = :productId
              and p.stock >= :quantity
            """)
    int updateProductStockByProductId(@Param("productId") Long productId, @Param("quantity") Long quantity);
}
