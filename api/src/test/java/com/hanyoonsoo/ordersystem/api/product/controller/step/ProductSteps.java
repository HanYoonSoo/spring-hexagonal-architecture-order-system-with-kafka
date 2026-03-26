package com.hanyoonsoo.ordersystem.api.product.controller.step;

import com.hanyoonsoo.ordersystem.api.product.dto.request.CreateProductRequest;
import io.restassured.module.mockmvc.response.ValidatableMockMvcResponse;
import org.springframework.http.MediaType;

import static io.restassured.module.mockmvc.RestAssuredMockMvc.given;

public final class ProductSteps {

    private ProductSteps() {
    }

    public static CreateProductRequest 상품생성요청_생성() {
        return new CreateProductRequest("상품", "설명", 1000L, 10L);
    }

    public static ValidatableMockMvcResponse 상품생성요청(CreateProductRequest request) {
        return given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(request)
                .when()
                .post("/api/v1/products")
                .then();
    }

    public static ValidatableMockMvcResponse 상품조회요청(Long productId) {
        return given()
                .when()
                .get("/api/v1/products/{productId}", productId)
                .then();
    }

    public static ValidatableMockMvcResponse 최근본상품추가요청(Long productId) {
        return given()
                .when()
                .post("/api/v1/products/{productId}/recently-viewed", productId)
                .then();
    }

    public static ValidatableMockMvcResponse 최근본상품목록조회요청() {
        return given()
                .when()
                .get("/api/v1/products/recently-viewed")
                .then();
    }
}
