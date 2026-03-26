package com.hanyoonsoo.ordersystem.core.domain.user.entity;

import com.hanyoonsoo.ordersystem.core.support.fixture.UserFixture;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class UserEntityTest {

    @Test
    void 사용자_권한을_생성하면_유저와_연관관계가_설정된다() {
        // given
        User user = UserFixture.사용자();

        // when
        UserRole userRole = UserFixture.사용자_역할(user);

        // then
        assertThat(userRole.getRole()).isEqualTo(Role.USER);
        assertThat(user.getUserRoles()).contains(userRole);
    }

    @Test
    void 사용자_인증정보를_생성하면_유저와_연관관계가_설정된다() {
        // given
        User user = UserFixture.사용자();

        // when
        UserCredential userCredential = UserFixture.사용자_인증정보(user);

        // then
        assertThat(userCredential.getLoginId()).isEqualTo("test@example.com");
        assertThat(userCredential.getProvider()).isEqualTo(CredentialProvider.LOCAL);
        assertThat(user.getUserCredentials()).contains(userCredential);
    }

    @Test
    void 인증정보_값을_변경하면_새_값으로_반영된다() {
        // given
        UserCredential userCredential = UserFixture.사용자_인증정보(UserFixture.사용자());

        // when
        userCredential.modifyValue("new-encoded-password");

        // then
        assertThat(userCredential.getValue()).isEqualTo("new-encoded-password");
    }
}
