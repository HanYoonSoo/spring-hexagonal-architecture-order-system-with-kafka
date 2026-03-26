package com.hanyoonsoo.ordersystem.api.user.controller;

import com.hanyoonsoo.ordersystem.api.auth.utils.SecurityUtils;
import com.hanyoonsoo.ordersystem.api.common.exception.GlobalExceptionHandler;
import com.hanyoonsoo.ordersystem.api.user.controller.step.UserSteps;
import com.hanyoonsoo.ordersystem.application.auth.dto.JwtUserClaims;
import com.hanyoonsoo.ordersystem.application.user.dto.UserDetailResult;
import com.hanyoonsoo.ordersystem.application.user.port.in.UserServicePort;
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
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock
    private UserServicePort userService;
    @InjectMocks
    private UserController userController;

    @BeforeEach
    void setUp() {
        RestAssuredMockMvc.mockMvc(
                MockMvcBuilders.standaloneSetup(userController)
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
    void 내_정보를_조회하면_응답으로_반환한다() {
        // given
        UUID userId = UUID.randomUUID();
        인증된_사용자를_설정한다(userId);
        given(userService.getMyInfo(userId)).willReturn(new UserDetailResult(userId, "하니", List.of(Role.USER)));

        // when & then
        UserSteps.내정보조회요청()
                .statusCode(200)
                .body("success", equalTo(true))
                .body("data.id", equalTo(userId.toString()))
                .body("data.name", equalTo("하니"))
                .body("data.roles[0]", equalTo("USER"));
    }

    @Test
    void 회원가입을_요청하면_성공_응답을_반환한다() {
        // given

        // when & then
        UserSteps.회원가입요청(UserSteps.회원가입요청_생성())
                .statusCode(200)
                .body("success", equalTo(true))
                .body("data", nullValue());

        then(userService).should().signUp(any());
    }

    private void 인증된_사용자를_설정한다(UUID userId) {
        JwtUserClaims claims = new JwtUserClaims(userId, List.of(Role.USER));
        PreAuthenticatedAuthenticationToken authentication = new PreAuthenticatedAuthenticationToken(claims, null);
        org.springframework.security.core.context.SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}
