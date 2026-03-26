package com.hanyoonsoo.ordersystem.application.product.service;

import com.hanyoonsoo.ordersystem.application.product.dto.CreateProductCommand;
import com.hanyoonsoo.ordersystem.application.product.dto.ProductDetailResult;
import com.hanyoonsoo.ordersystem.application.product.port.out.ProductRepository;
import com.hanyoonsoo.ordersystem.application.product.port.out.RecentlyViewedProductRepository;
import com.hanyoonsoo.ordersystem.application.support.fixture.ProductFixture;
import com.hanyoonsoo.ordersystem.common.exception.base.NotFoundException;
import com.hanyoonsoo.ordersystem.core.domain.product.entity.Product;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;
    @Mock
    private RecentlyViewedProductRepository recentlyViewedProductRepository;
    @InjectMocks
    private ProductService productService;

    @Test
    void 상품_상세를_조회하면_상품_상세_결과를_반환한다() {
        // given
        Product product = ProductFixture.상품(1L);
        given(productRepository.findProductById(1L)).willReturn(Optional.of(product));

        // when
        ProductDetailResult result = productService.getProductInfo(1L);

        // then
        assertThat(result.id()).isEqualTo(1L);
        assertThat(result.name()).isEqualTo("keyboard");
    }

    @Test
    void 상품_상세_조회시_상품이_없으면_예외가_발생한다() {
        // given
        given(productRepository.findProductById(1L)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> productService.getProductInfo(1L))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void 상품_생성시_명령으로부터_엔티티를_저장한다() {
        // given
        CreateProductCommand command = ProductFixture.상품생성명령();

        // when
        productService.createProduct(command);

        // then
        ArgumentCaptor<Product> productCaptor = ArgumentCaptor.forClass(Product.class);
        then(productRepository).should().save(productCaptor.capture());
        assertThat(productCaptor.getValue().getName()).isEqualTo("keyboard");
    }

    @Test
    void 최근_본_상품_추가는_저장소에_위임한다() {
        // given
        UUID userId = UUID.randomUUID();

        // when
        productService.addRecentlyViewedProduct(userId, 1L);

        // then
        then(recentlyViewedProductRepository).should().addRecentlyViewedProduct(userId, 1L);
    }

    @Test
    void 최근_본_상품_목록은_상품을_결과로_매핑해_반환한다() {
        // given
        UUID userId = UUID.randomUUID();
        given(recentlyViewedProductRepository.getRecentlyViewedProducts(userId)).willReturn(List.of(1L, 2L));
        given(productRepository.findProductsByIds(List.of(1L, 2L))).willReturn(ProductFixture.상품목록(1L, 2L));

        // when
        List<ProductDetailResult> results = productService.getRecentlyViewedProducts(userId);

        // then
        assertThat(results).hasSize(2);
        assertThat(results).extracting(ProductDetailResult::id).containsExactly(1L, 2L);
    }
}
