package com.hanyoonsoo.ordersystem.api.auth.controller;

import com.hanyoonsoo.ordersystem.api.auth.config.CookieProperties;
import com.hanyoonsoo.ordersystem.api.auth.config.CorsAllowedOriginsProperties;
import com.hanyoonsoo.ordersystem.api.auth.controller.step.AuthSteps;
import com.hanyoonsoo.ordersystem.api.common.exception.GlobalExceptionHandler;
import com.hanyoonsoo.ordersystem.application.auth.dto.TokenResult;
import com.hanyoonsoo.ordersystem.application.auth.port.in.AuthServicePort;
import io.restassured.module.mockmvc.RestAssuredMockMvc;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.Duration;
import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.cookie;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private AuthServicePort authService;
    @Mock
    private Environment environment;

    private AuthController authController;
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        CorsAllowedOriginsProperties corsAllowedOriginsProperties = new CorsAllowedOriginsProperties();
        corsAllowedOriginsProperties.setOrigins(List.of("https://frontend.example.com"));
        CookieProperties cookieProperties = new CookieProperties();
        cookieProperties.setSameSite("Lax");
        cookieProperties.setSecure(false);
        authController = new AuthController(authService, corsAllowedOriginsProperties, cookieProperties, environment);

        mockMvc = MockMvcBuilders.standaloneSetup(authController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
        RestAssuredMockMvc.mockMvc(mockMvc);
    }

    @AfterEach
    void tearDown() {
        RestAssuredMockMvc.reset();
    }

    @Test
    void 로그인에_성공하면_응답_헤더와_쿠키에_토큰을_설정한다() {
        // given
        given(authService.signIn(any())).willReturn(new TokenResult("access-token", "refresh-token", Duration.ofDays(7)));

        // when & then
        AuthSteps.로그인요청(AuthSteps.로그인요청_생성())
                .statusCode(200)
                .header(HttpHeaders.AUTHORIZATION, equalTo("Bearer access-token"))
                .header(HttpHeaders.SET_COOKIE, containsString("s_rt=refresh-token"))
                .body("success", equalTo(true));

        then(authService).should().signIn(any());
    }

    @Test
    void 토큰_재발급에_성공하면_새_토큰을_응답에_설정한다() {
        // given
        given(environment.getActiveProfiles()).willReturn(new String[]{"prod"});
        given(authService.reissue("access-token", "refresh-token"))
                .willReturn(new TokenResult("new-access-token", "new-refresh-token", Duration.ofDays(7)));

        // when & then
        try {
            mockMvc.perform(post("/api/v1/auth/reissue")
                            .header(HttpHeaders.AUTHORIZATION, "Bearer access-token")
                            .header(HttpHeaders.ORIGIN, "https://frontend.example.com")
                            .cookie(new Cookie(AuthServicePort.REFRESH_TOKEN_COOKIE_NAME, "refresh-token")))
                    .andExpect(status().isOk())
                    .andExpect(header().string(HttpHeaders.AUTHORIZATION, "Bearer new-access-token"))
                    .andExpect(cookie().value(AuthServicePort.REFRESH_TOKEN_COOKIE_NAME, "new-refresh-token"));
        } catch (Exception exception) {
            throw new RuntimeException(exception);
        }
    }

    @Test
    void Bearer_형식이_아닌_재발급_요청은_인증_예외를_반환한다() {
        // given
        given(environment.getActiveProfiles()).willReturn(new String[0]);

        // when & then
        try {
            mockMvc.perform(post("/api/v1/auth/reissue")
                            .header(HttpHeaders.AUTHORIZATION, "invalid-token")
                            .cookie(new Cookie(AuthServicePort.REFRESH_TOKEN_COOKIE_NAME, "refresh-token")))
                    .andExpect(status().isUnauthorized());
        } catch (Exception exception) {
            throw new RuntimeException(exception);
        }
    }
}
