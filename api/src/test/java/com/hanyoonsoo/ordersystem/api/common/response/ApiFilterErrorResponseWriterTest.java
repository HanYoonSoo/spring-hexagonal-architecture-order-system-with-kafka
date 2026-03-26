package com.hanyoonsoo.ordersystem.api.common.response;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.hanyoonsoo.ordersystem.api.auth.config.CorsAllowedOriginsProperties;
import com.hanyoonsoo.ordersystem.common.exception.ErrorCode;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ApiFilterErrorResponseWriterTest {

    @Test
    void 에러_응답을_작성하면_CORS_헤더와_본문을_설정한다() throws Exception {
        // given
        CorsAllowedOriginsProperties properties = new CorsAllowedOriginsProperties();
        properties.setOrigins(List.of("https://frontend.example.com"));
        ObjectMapper objectMapper = new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        ApiFilterErrorResponseWriter writer = new ApiFilterErrorResponseWriter(objectMapper, properties);
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader(HttpHeaders.ORIGIN, "https://frontend.example.com");
        MockHttpServletResponse response = new MockHttpServletResponse();

        // when
        writer.write(request, response, ErrorCode.INVALID_REQUEST, "잘못된 요청입니다.", "/api/test");

        // then
        assertThat(response.getStatus()).isEqualTo(400);
        assertThat(response.getHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN)).isEqualTo("https://frontend.example.com");
        assertThat(response.getContentType()).startsWith("application/json");
        assertThat(response.getContentAsString()).contains("\"success\":false");
        assertThat(response.getContentAsString()).contains("\"code\":\"A001\"");
    }
}
