package com.hanyoonsoo.ordersystem.api.auth.custom;

import com.hanyoonsoo.ordersystem.api.auth.exception.JwtAuthenticationException;
import com.hanyoonsoo.ordersystem.api.common.response.ApiFilterErrorResponseWriter;
import com.hanyoonsoo.ordersystem.common.exception.ErrorCode;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.BadCredentialsException;

import static org.mockito.Mockito.eq;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class CustomAuthenticationEntryPointTest {

    @Mock
    private ApiFilterErrorResponseWriter apiFilterErrorResponseWriter;
    @InjectMocks
    private CustomAuthenticationEntryPoint customAuthenticationEntryPoint;

    @Test
    void JWT_예외가_주어지면_JWT_전용_에러코드를_사용한다() throws Exception {
        // given
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/v1/orders");
        MockHttpServletResponse response = new MockHttpServletResponse();
        JwtAuthenticationException exception = new JwtAuthenticationException(ErrorCode.ACCESS_TOKEN_EXPIRED, "expired");

        // when
        customAuthenticationEntryPoint.commence(request, response, exception);

        // then
        then(apiFilterErrorResponseWriter).should().write(
                eq(request),
                eq(response),
                eq(ErrorCode.ACCESS_TOKEN_EXPIRED),
                eq("expired"),
                eq("/api/v1/orders")
        );
    }

    @Test
    void 일반_인증_예외가_주어지면_기본_인증_실패_코드를_사용한다() throws Exception {
        // given
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/v1/orders");
        MockHttpServletResponse response = new MockHttpServletResponse();

        // when
        customAuthenticationEntryPoint.commence(request, response, new BadCredentialsException("bad credentials"));

        // then
        then(apiFilterErrorResponseWriter).should().write(
                eq(request),
                eq(response),
                eq(ErrorCode.AUTHENTICATION_FAILED),
                eq("bad credentials"),
                eq("/api/v1/orders")
        );
    }
}
