package com.hanyoonsoo.ordersystem.api.order.controller.step;

import com.hanyoonsoo.ordersystem.api.order.dto.request.CreateOrderRequest;
import io.restassured.module.mockmvc.response.ValidatableMockMvcResponse;
import org.springframework.http.MediaType;

import static io.restassured.module.mockmvc.RestAssuredMockMvc.given;

public final class OrderSteps {

    private OrderSteps() {
    }

    public static CreateOrderRequest 주문생성요청_생성(Long productId, Long quantity) {
        return new CreateOrderRequest(productId, quantity);
    }

    public static ValidatableMockMvcResponse 주문생성요청(CreateOrderRequest request) {
        return given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(request)
                .when()
                .post("/api/v1/orders")
                .then();
    }
}
