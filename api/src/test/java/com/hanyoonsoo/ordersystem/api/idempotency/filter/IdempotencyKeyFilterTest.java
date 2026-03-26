package com.hanyoonsoo.ordersystem.api.idempotency.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.hanyoonsoo.ordersystem.api.auth.config.CorsAllowedOriginsProperties;
import com.hanyoonsoo.ordersystem.api.common.response.ApiFilterErrorResponseWriter;
import com.hanyoonsoo.ordersystem.application.idempotency.model.IdempotencyKeyMetadata;
import com.hanyoonsoo.ordersystem.application.idempotency.port.in.IdempotencyServicePort;
import io.restassured.module.mockmvc.RestAssuredMockMvc;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

import static io.restassured.module.mockmvc.RestAssuredMockMvc.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class IdempotencyKeyFilterTest {

    @Mock
    private IdempotencyServicePort idempotencyServicePort;
    private IdempotencyKeyFilter idempotencyKeyFilter;

    @BeforeEach
    void setUp() {
        ObjectMapper objectMapper = new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        CorsAllowedOriginsProperties corsAllowedOriginsProperties = new CorsAllowedOriginsProperties();
        corsAllowedOriginsProperties.setOrigins(List.of("https://frontend.example.com"));
        ApiFilterErrorResponseWriter writer = new ApiFilterErrorResponseWriter(objectMapper, corsAllowedOriginsProperties);
        idempotencyKeyFilter = new IdempotencyKeyFilter(idempotencyServicePort, writer);

        RestAssuredMockMvc.mockMvc(
                MockMvcBuilders.standaloneSetup(new TestController())
                        .addFilters(idempotencyKeyFilter)
                        .build()
        );
    }

    @AfterEach
    void tearDown() {
        RestAssuredMockMvc.reset();
    }

    @Test
    void 멱등성_키_저장에_성공하면_요청을_통과시킨다() {
        // given
        org.mockito.BDDMockito.given(idempotencyServicePort.saveIfAbsentIdempotencyKey(eq("idem-key"), any(IdempotencyKeyMetadata.class)))
                .willReturn(true);

        // when & then
        given()
                .header("X-Idempotency-Key", "idem-key")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
        .when()
                .post("/api/v1/orders")
        .then()
                .statusCode(200)
                .body("result", equalTo("ok"));

        ArgumentCaptor<IdempotencyKeyMetadata> metadataCaptor = ArgumentCaptor.forClass(IdempotencyKeyMetadata.class);
        then(idempotencyServicePort).should().saveIfAbsentIdempotencyKey(eq("idem-key"), metadataCaptor.capture());
        assertThat(metadataCaptor.getValue().method()).isEqualTo("POST");
        assertThat(metadataCaptor.getValue().path()).isEqualTo("/api/v1/orders");
    }

    @Test
    void 멱등성_키가_이미_존재하면_중복_요청을_차단한다() {
        // given
        org.mockito.BDDMockito.given(idempotencyServicePort.saveIfAbsentIdempotencyKey(eq("idem-key"), any(IdempotencyKeyMetadata.class)))
                .willReturn(false);

        // when & then
        given()
                .header("X-Idempotency-Key", "idem-key")
                .header("Origin", "https://frontend.example.com")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
        .when()
                .post("/api/v1/orders")
        .then()
                .statusCode(409)
                .header("Access-Control-Allow-Origin", equalTo("https://frontend.example.com"))
                .body("success", equalTo(false))
                .body("error.code", equalTo("A022"));
    }

    @Test
    void 허용된_경로는_멱등성_검사를_건너뛴다() {
        // given
        MockHttpServletRequest request = new MockHttpServletRequest("POST", "/api/v1/auth/sign-in");
        request.addHeader("X-Idempotency-Key", "idem-key");
        request.setServletPath("/api/v1/auth/sign-in");

        // when
        boolean actual = idempotencyKeyFilter.shouldNotFilter(request);

        // then
        assertThat(actual).isTrue();
    }

    @RestController
    @RequestMapping("/api/v1")
    static class TestController {

        @PostMapping("/orders")
        Map<String, String> order() {
            return Map.of("result", "ok");
        }

        @PostMapping("/auth/sign-in")
        Map<String, String> signIn() {
            return Map.of("result", "ok");
        }
    }
}
