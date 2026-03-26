package com.hanyoonsoo.ordersystem.application.user.model;

import com.hanyoonsoo.ordersystem.common.exception.base.BadRequestException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class EmailPasswordCredentialTest {

    @Test
    void 인증정보_형식이_올바르면_이메일과_비밀번호를_파싱한다() {
        // given
        String credential = "test@example.com:password12";

        // when
        EmailPasswordCredential actual = EmailPasswordCredential.from(credential);

        // then
        assertThat(actual.loginId()).isEqualTo("test@example.com");
        assertThat(actual.plainPassword()).isEqualTo("password12");
    }

    @Test
    void 인증정보_형식이_올바르지_않으면_예외가_발생한다() {
        // given
        String credential = "invalid-format";

        // when & then
        assertThatThrownBy(() -> EmailPasswordCredential.from(credential))
                .isInstanceOf(BadRequestException.class);
    }

    @Test
    void 이메일_형식이_올바르지_않으면_예외가_발생한다() {
        // given
        String credential = "not-email:password12";

        // when & then
        assertThatThrownBy(() -> EmailPasswordCredential.from(credential))
                .isInstanceOf(BadRequestException.class);
    }

    @Test
    void 비밀번호_길이가_올바르지_않으면_예외가_발생한다() {
        // given
        String credential = "test@example.com:short";

        // when & then
        assertThatThrownBy(() -> EmailPasswordCredential.from(credential))
                .isInstanceOf(BadRequestException.class);
    }
}
