package com.hanyoonsoo.ordersystem.core.domain.product.entity;

import com.hanyoonsoo.ordersystem.core.support.fixture.ProductFixture;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ProductTest {

    @Test
    void 상품을_생성하면_입력한_필드값을_가진다() {
        // given
        Product product = ProductFixture.상품();

        // when
        String name = product.getName();

        // then
        assertThat(name).isEqualTo("keyboard");
        assertThat(product.getDescription()).isEqualTo("mechanical");
        assertThat(product.getPrice()).isEqualTo(10000L);
        assertThat(product.getStock()).isEqualTo(5L);
    }
}
