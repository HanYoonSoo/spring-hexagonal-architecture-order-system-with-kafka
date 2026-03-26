package com.hanyoonsoo.ordersystem.api.user.controller.step;

import com.hanyoonsoo.ordersystem.api.user.dto.request.SignUpRequest;
import io.restassured.module.mockmvc.response.ValidatableMockMvcResponse;
import org.springframework.http.MediaType;

import static io.restassured.module.mockmvc.RestAssuredMockMvc.given;

public final class UserSteps {

    private UserSteps() {
    }

    public static SignUpRequest 회원가입요청_생성() {
        return new SignUpRequest("하니", "test@example.com:test1234", null, null);
    }

    public static ValidatableMockMvcResponse 회원가입요청(SignUpRequest request) {
        return given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(request)
                .when()
                .post("/api/v1/users")
                .then();
    }

    public static ValidatableMockMvcResponse 내정보조회요청() {
        return given()
                .when()
                .get("/api/v1/users/me")
                .then();
    }
}
