package com.hanyoonsoo.ordersystem.api.product.controller;

import com.hanyoonsoo.ordersystem.api.auth.utils.SecurityUtils;
import com.hanyoonsoo.ordersystem.api.common.exception.GlobalExceptionHandler;
import com.hanyoonsoo.ordersystem.api.product.controller.step.ProductSteps;
import com.hanyoonsoo.ordersystem.application.auth.dto.JwtUserClaims;
import com.hanyoonsoo.ordersystem.application.product.dto.ProductDetailResult;
import com.hanyoonsoo.ordersystem.application.product.port.in.ProductServicePort;
import com.hanyoonsoo.ordersystem.core.domain.user.entity.Role;
import io.restassured.module.mockmvc.RestAssuredMockMvc;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class ProductControllerTest {

    @Mock
    private ProductServicePort productService;
    @InjectMocks
    private ProductController productController;

    @BeforeEach
    void setUp() {
        RestAssuredMockMvc.mockMvc(
                MockMvcBuilders.standaloneSetup(productController)
                        .setControllerAdvice(new GlobalExceptionHandler())
                        .build()
        );
    }

    @AfterEach
    void tearDown() {
        SecurityUtils.clear();
        RestAssuredMockMvc.reset();
    }

    @Test
    void 상품_상세를_조회하면_응답으로_반환한다() {
        // given
        given(productService.getProductInfo(1L)).willReturn(new ProductDetailResult(1L, "상품", "설명", 1000L, 10L));

        // when & then
        ProductSteps.상품조회요청(1L)
                .statusCode(200)
                .body("success", equalTo(true))
                .body("data.productId", equalTo(1))
                .body("data.name", equalTo("상품"))
                .body("data.stock", equalTo(10));
    }

    @Test
    void 상품을_생성하면_성공_응답을_반환한다() {
        // given

        // when & then
        ProductSteps.상품생성요청(ProductSteps.상품생성요청_생성())
                .statusCode(200)
                .body("success", equalTo(true))
                .body("data", nullValue());

        then(productService).should().createProduct(any());
    }

    @Test
    void 최근_본_상품으로_추가하면_인증된_사용자_ID와_함께_위임한다() {
        // given
        UUID userId = UUID.randomUUID();
        인증된_사용자를_설정한다(userId);

        // when & then
        ProductSteps.최근본상품추가요청(1L)
                .statusCode(200)
                .body("success", equalTo(true));

        then(productService).should().addRecentlyViewedProduct(userId, 1L);
    }

    @Test
    void 최근_본_상품_목록을_조회하면_응답으로_반환한다() {
        // given
        UUID userId = UUID.randomUUID();
        인증된_사용자를_설정한다(userId);
        given(productService.getRecentlyViewedProducts(userId)).willReturn(List.of(
                new ProductDetailResult(1L, "상품1", "설명1", 1000L, 10L),
                new ProductDetailResult(2L, "상품2", "설명2", 2000L, 20L)
        ));

        // when & then
        ProductSteps.최근본상품목록조회요청()
                .statusCode(200)
                .body("success", equalTo(true))
                .body("data.recentlyViewedProducts", hasSize(2))
                .body("data.recentlyViewedProducts[0].id", equalTo(1))
                .body("data.recentlyViewedProducts[1].id", equalTo(2));
    }

    private void 인증된_사용자를_설정한다(UUID userId) {
        JwtUserClaims claims = new JwtUserClaims(userId, List.of(Role.USER));
        PreAuthenticatedAuthenticationToken authentication = new PreAuthenticatedAuthenticationToken(claims, null);
        org.springframework.security.core.context.SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}
