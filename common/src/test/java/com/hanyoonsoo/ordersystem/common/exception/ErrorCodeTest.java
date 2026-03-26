package com.hanyoonsoo.ordersystem.common.exception;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ErrorCodeTest {

    @Test
    void 상세_메시지가_비어있으면_기본_메시지를_반환한다() {
        // given
        ErrorCode errorCode = ErrorCode.INVALID_REQUEST;

        // when
        String message = errorCode.message(" ");

        // then
        assertThat(message).isEqualTo(errorCode.getMessage());
    }

    @Test
    void 상세_메시지가_있으면_기본_메시지에_추가한다() {
        // given
        ErrorCode errorCode = ErrorCode.INVALID_REQUEST;

        // when
        String message = errorCode.message("quantity");

        // then
        assertThat(message).isEqualTo("잘못된 요청입니다. (quantity)");
    }
}
