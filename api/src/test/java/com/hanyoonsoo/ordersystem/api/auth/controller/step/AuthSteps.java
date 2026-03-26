package com.hanyoonsoo.ordersystem.api.auth.controller.step;

import com.hanyoonsoo.ordersystem.api.auth.dto.SignInRequest;
import com.hanyoonsoo.ordersystem.application.auth.port.in.AuthServicePort;
import io.restassured.module.mockmvc.response.ValidatableMockMvcResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import static io.restassured.module.mockmvc.RestAssuredMockMvc.given;

public final class AuthSteps {

    private AuthSteps() {
    }

    public static SignInRequest 로그인요청_생성() {
        return new SignInRequest("test@example.com:test1234", null);
    }

    public static ValidatableMockMvcResponse 로그인요청(SignInRequest request) {
        return given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(request)
                .when()
                .post("/api/v1/auth/sign-in")
                .then();
    }

    public static ValidatableMockMvcResponse 토큰재발급요청(String authorization, String origin, String refreshToken) {
        var requestSpecification = given()
                .header(HttpHeaders.AUTHORIZATION, authorization)
                .header(HttpHeaders.COOKIE, AuthServicePort.REFRESH_TOKEN_COOKIE_NAME + "=" + refreshToken);

        if (origin != null) {
            requestSpecification.header(HttpHeaders.ORIGIN, origin);
        }

        return requestSpecification
                .when()
                .post("/api/v1/auth/reissue")
                .then();
    }
}
