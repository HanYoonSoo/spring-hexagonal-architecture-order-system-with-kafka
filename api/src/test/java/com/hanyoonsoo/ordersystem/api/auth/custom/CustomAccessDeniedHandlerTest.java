package com.hanyoonsoo.ordersystem.api.auth.custom;

import com.hanyoonsoo.ordersystem.api.common.response.ApiFilterErrorResponseWriter;
import com.hanyoonsoo.ordersystem.common.exception.ErrorCode;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.access.AccessDeniedException;

import static org.mockito.Mockito.eq;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class CustomAccessDeniedHandlerTest {

    @Mock
    private ApiFilterErrorResponseWriter apiFilterErrorResponseWriter;
    @InjectMocks
    private CustomAccessDeniedHandler customAccessDeniedHandler;

    @Test
    void 접근_거부_처리를_에러_응답_작성기에_위임한다() throws Exception {
        // given
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/v1/orders");
        MockHttpServletResponse response = new MockHttpServletResponse();

        // when
        customAccessDeniedHandler.handle(request, response, new AccessDeniedException("forbidden"));

        // then
        then(apiFilterErrorResponseWriter).should().write(
                eq(request),
                eq(response),
                eq(ErrorCode.FORBIDDEN_USER),
                eq("forbidden"),
                eq("/api/v1/orders")
        );
    }
}
