package com.hanyoonsoo.ordersystem.adapter.out.persistence.jpa.product.adapter;

import com.hanyoonsoo.ordersystem.adapter.support.container.IntegrationTestContainerSupporter;
import com.hanyoonsoo.ordersystem.core.domain.product.entity.Product;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class ProductRepositoryAdapterTest extends IntegrationTestContainerSupporter {

    @Autowired
    private ProductRepositoryAdapter productRepositoryAdapter;

    @Test
    void 상품_ID_존재_여부를_확인한다() {
        // given
        Product saved = Product.of("keyboard", "mechanical", 10000L, 5L);
        productRepositoryAdapter.save(saved);

        // when
        boolean actual = productRepositoryAdapter.existsById(saved.getId());

        // then
        assertThat(actual).isTrue();
    }

    @Test
    void 상품_ID로_상품을_조회한다() {
        // given
        Product saved = Product.of("keyboard", "mechanical", 10000L, 5L);
        productRepositoryAdapter.save(saved);

        // when
        Optional<Product> actual = productRepositoryAdapter.findProductById(saved.getId());

        // then
        assertThat(actual).isPresent();
        assertThat(actual.get().getName()).isEqualTo("keyboard");
    }

    @Test
    void 요청한_ID_순서를_보존해서_상품을_조회한다() {
        // given
        Product first = Product.of("product-1", "desc-1", 1000L, 5L);
        Product second = Product.of("product-2", "desc-2", 2000L, 6L);
        productRepositoryAdapter.save(first);
        productRepositoryAdapter.save(second);

        // when
        List<Product> actual = productRepositoryAdapter.findProductsByIds(List.of(second.getId(), first.getId()));

        // then
        assertThat(actual).extracting(Product::getId).containsExactly(second.getId(), first.getId());
    }

    @Test
    void 상품_재고를_조회한다() {
        // given
        Product saved = Product.of("keyboard", "mechanical", 10000L, 10L);
        productRepositoryAdapter.save(saved);

        // when
        Optional<Long> actual = productRepositoryAdapter.findStockByProductId(saved.getId());

        // then
        assertThat(actual).contains(10L);
    }

    @Test
    @Transactional
    void 재고_차감_성공_여부를_반환한다() {
        // given
        Product saved = Product.of("keyboard", "mechanical", 10000L, 10L);
        productRepositoryAdapter.save(saved);

        // when
        boolean actual = productRepositoryAdapter.decreaseStock(saved.getId(), 3L);
        Optional<Long> remainingStock = productRepositoryAdapter.findStockByProductId(saved.getId());

        // then
        assertThat(actual).isTrue();
        assertThat(remainingStock).contains(7L);
    }
}
