package com.hanyoonsoo.ordersystem.adapter.support.fixture;

import com.hanyoonsoo.ordersystem.core.domain.user.entity.CredentialProvider;
import com.hanyoonsoo.ordersystem.core.domain.user.entity.Role;
import com.hanyoonsoo.ordersystem.core.domain.user.entity.User;
import com.hanyoonsoo.ordersystem.core.domain.user.entity.UserCredential;
import com.hanyoonsoo.ordersystem.core.domain.user.entity.UserRole;

public final class UserFixture {

    private UserFixture() {
    }

    public static User 사용자() {
        return User.from("tester");
    }

    public static UserRole 사용자_역할(User user) {
        return UserRole.of(Role.USER, user);
    }

    public static UserCredential 사용자_인증정보(User user) {
        return UserCredential.of("test@example.com", "encoded-password", CredentialProvider.LOCAL, user);
    }
}
