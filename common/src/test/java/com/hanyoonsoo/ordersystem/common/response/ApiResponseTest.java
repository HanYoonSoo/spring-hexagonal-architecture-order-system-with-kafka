package com.hanyoonsoo.ordersystem.common.response;

import com.hanyoonsoo.ordersystem.common.exception.ErrorResponse;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ApiResponseTest {

    @Test
    void 성공_응답을_생성한다() {
        // given
        String data = "value";

        // when
        ApiResponse<String> response = ApiResponse.success(data);

        // then
        assertThat(response.isSuccess()).isTrue();
        assertThat(response.getData()).isEqualTo(data);
        assertThat(response.getError()).isNull();
    }

    @Test
    void 실패_응답을_생성한다() {
        // given
        ErrorResponse errorResponse = ErrorResponse.of("A001", "bad request", "/api/test");

        // when
        ApiResponse<Void> response = ApiResponse.failure(errorResponse);

        // then
        assertThat(response.isSuccess()).isFalse();
        assertThat(response.getData()).isNull();
        assertThat(response.getError()).isEqualTo(errorResponse);
    }
}
