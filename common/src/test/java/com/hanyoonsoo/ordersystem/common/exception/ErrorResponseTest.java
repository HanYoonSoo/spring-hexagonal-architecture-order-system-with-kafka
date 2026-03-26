package com.hanyoonsoo.ordersystem.common.exception;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ErrorResponseTest {

    @Test
    void 에러_응답을_생성하면_현재_시각이_포함된다() {
        // given
        String code = "A001";
        String message = "bad request";
        String path = "/api/test";

        // when
        ErrorResponse errorResponse = ErrorResponse.of(code, message, path);

        // then
        assertThat(errorResponse.getTimestamp()).isNotNull();
        assertThat(errorResponse.getCode()).isEqualTo(code);
        assertThat(errorResponse.getMessage()).isEqualTo(message);
        assertThat(errorResponse.getPath()).isEqualTo(path);
    }
}
