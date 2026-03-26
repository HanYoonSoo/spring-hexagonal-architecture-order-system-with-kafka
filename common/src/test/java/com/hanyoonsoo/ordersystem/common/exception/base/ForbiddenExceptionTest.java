package com.hanyoonsoo.ordersystem.common.exception.base;

import com.hanyoonsoo.ordersystem.common.exception.ErrorCode;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ForbiddenExceptionTest {

    @Test
    void 에러코드로_생성하면_기본_상태값과_메시지를_가진다() {
        // given

        // when
        ForbiddenException actual = new ForbiddenException(ErrorCode.FORBIDDEN_USER);

        // then
        assertThat(actual.status()).isEqualTo(403);
        assertThat(actual.code()).isEqualTo("A013");
        assertThat(actual.getMessage()).isEqualTo("권한이 없는 유저입니다.");
    }

    @Test
    void 사용자_메시지로_생성하면_해당_메시지를_사용한다() {
        // given

        // when
        ForbiddenException actual = new ForbiddenException(ErrorCode.FORBIDDEN_USER, "권한 부족");

        // then
        assertThat(actual.getMessage()).isEqualTo("권한 부족");
    }
}
