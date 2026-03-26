package com.hanyoonsoo.ordersystem.api.order.controller;

import com.hanyoonsoo.ordersystem.api.auth.utils.SecurityUtils;
import com.hanyoonsoo.ordersystem.api.common.exception.GlobalExceptionHandler;
import com.hanyoonsoo.ordersystem.api.order.controller.step.OrderSteps;
import com.hanyoonsoo.ordersystem.application.auth.dto.JwtUserClaims;
import com.hanyoonsoo.ordersystem.application.order.port.in.OrderServicePort;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class OrderControllerTest {

    @Mock
    private OrderServicePort orderService;
    @InjectMocks
    private OrderController orderController;

    @BeforeEach
    void setUp() {
        RestAssuredMockMvc.mockMvc(
                MockMvcBuilders.standaloneSetup(orderController)
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
    void 주문_생성_요청이_성공하면_주문_ID와_PENDING_상태를_반환한다() {
        // given
        UUID userId = UUID.randomUUID();
        UUID orderId = UUID.randomUUID();
        인증된_사용자를_설정한다(userId);
        given(orderService.requestOrder(any())).willReturn(orderId);

        // when & then
        OrderSteps.주문생성요청(OrderSteps.주문생성요청_생성(1L, 3L))
                .statusCode(200)
                .body("success", equalTo(true))
                .body("data.orderId", equalTo(orderId.toString()))
                .body("data.status", equalTo("PENDING"));

        then(orderService).should().requestOrder(any());
    }

    @Test
    void 주문_생성_요청의_수량이_0이면_검증_예외를_반환한다() {
        // given
        인증된_사용자를_설정한다(UUID.randomUUID());

        // when & then
        OrderSteps.주문생성요청(OrderSteps.주문생성요청_생성(1L, 0L))
                .statusCode(400)
                .body("success", equalTo(false))
                .body("error.code", equalTo("A001"));
    }

    private void 인증된_사용자를_설정한다(UUID userId) {
        JwtUserClaims claims = new JwtUserClaims(userId, List.of(Role.USER));
        PreAuthenticatedAuthenticationToken authentication = new PreAuthenticatedAuthenticationToken(claims, null);
        org.springframework.security.core.context.SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}
