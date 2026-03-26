package com.hanyoonsoo.ordersystem.adapter.out.persistence.jpa.user.adapter;

import com.hanyoonsoo.ordersystem.adapter.support.container.IntegrationTestContainerSupporter;
import com.hanyoonsoo.ordersystem.adapter.support.fixture.UserFixture;
import com.hanyoonsoo.ordersystem.core.domain.user.entity.CredentialProvider;
import com.hanyoonsoo.ordersystem.core.domain.user.entity.Role;
import com.hanyoonsoo.ordersystem.core.domain.user.entity.User;
import com.hanyoonsoo.ordersystem.core.domain.user.entity.UserCredential;
import com.hanyoonsoo.ordersystem.core.domain.user.entity.UserRole;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class UserRepositoryAdapterTest extends IntegrationTestContainerSupporter {

    @Autowired
    private UserRepositoryAdapter userRepositoryAdapter;

    @Test
    void 사용자를_저장한다() {
        // given
        User user = UserFixture.사용자();

        // when
        User actual = userRepositoryAdapter.save(user);

        // then
        assertThat(actual.getId()).isNotNull();
        assertThat(actual.getName()).isEqualTo("tester");
    }

    @Test
    void 사용자_역할을_저장한다() {
        // given
        User user = userRepositoryAdapter.save(UserFixture.사용자());
        UserRole userRole = UserFixture.사용자_역할(user);

        // when
        userRepositoryAdapter.saveUserRole(userRole);
        List<Role> actual = userRepositoryAdapter.findRolesByUserId(user.getId());

        // then
        assertThat(actual).containsExactly(Role.USER);
    }

    @Test
    void 사용자_인증정보를_저장한다() {
        // given
        User user = userRepositoryAdapter.save(UserFixture.사용자());
        UserCredential userCredential = UserFixture.사용자_인증정보(user);

        // when
        userRepositoryAdapter.saveUserCredential(userCredential);
        Optional<UserCredential> actual = userRepositoryAdapter.findUserCredentialByLoginIdAndProvider("test@example.com", CredentialProvider.LOCAL);

        // then
        assertThat(actual).isPresent();
        assertThat(actual.get().getValue()).isEqualTo("encoded-password");
    }

    @Test
    void 로그인_ID_중복_여부를_조회한다() {
        // given
        User user = userRepositoryAdapter.save(UserFixture.사용자());
        userRepositoryAdapter.saveUserCredential(UserFixture.사용자_인증정보(user));

        // when
        boolean actual = userRepositoryAdapter.existsByLoginId("test@example.com");

        // then
        assertThat(actual).isTrue();
    }

    @Test
    void 사용자_ID로_사용자를_조회한다() {
        // given
        User savedUser = userRepositoryAdapter.save(UserFixture.사용자());

        // when
        Optional<User> actual = userRepositoryAdapter.findById(savedUser.getId());

        // then
        assertThat(actual).isPresent();
        assertThat(actual.get().getId()).isEqualTo(savedUser.getId());
    }

    @Test
    void 로그인_ID와_프로바이더로_인증정보를_조회한다() {
        // given
        User user = userRepositoryAdapter.save(UserFixture.사용자());
        userRepositoryAdapter.saveUserCredential(UserFixture.사용자_인증정보(user));

        // when
        Optional<UserCredential> actual = userRepositoryAdapter.findUserCredentialByLoginIdAndProvider("test@example.com", CredentialProvider.LOCAL);

        // then
        assertThat(actual).isPresent();
        assertThat(actual.get().getLoginId()).isEqualTo("test@example.com");
    }

    @Test
    void 사용자_ID로_역할_목록을_조회한다() {
        // given
        User user = userRepositoryAdapter.save(UserFixture.사용자());
        userRepositoryAdapter.saveUserRole(UserFixture.사용자_역할(user));

        // when
        List<Role> actual = userRepositoryAdapter.findRolesByUserId(user.getId());

        // then
        assertThat(actual).containsExactly(Role.USER);
    }
}
