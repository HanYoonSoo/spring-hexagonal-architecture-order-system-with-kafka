package com.hanyoonsoo.ordersystem.application.support.fixture;

import com.hanyoonsoo.ordersystem.application.auth.dto.JwtUserClaims;
import com.hanyoonsoo.ordersystem.application.auth.dto.SignInCommand;
import com.hanyoonsoo.ordersystem.application.auth.dto.TokenResult;
import com.hanyoonsoo.ordersystem.application.user.dto.SignUpCommand;
import com.hanyoonsoo.ordersystem.core.domain.user.entity.CredentialProvider;
import com.hanyoonsoo.ordersystem.core.domain.user.entity.Role;
import com.hanyoonsoo.ordersystem.core.domain.user.entity.User;
import com.hanyoonsoo.ordersystem.core.domain.user.entity.UserCredential;

import java.time.Duration;
import java.util.List;
import java.util.UUID;

public final class UserFixture {

    private UserFixture() {
    }

    public static SignUpCommand 회원가입명령() {
        return new SignUpCommand("tester", "test@example.com:password12", CredentialProvider.LOCAL, Role.USER);
    }

    public static SignInCommand 로그인명령() {
        return new SignInCommand("test@example.com:password12", CredentialProvider.LOCAL);
    }

    public static User 사용자() {
        return User.from("tester");
    }

    public static UserCredential 인증정보(User user) {
        return UserCredential.of("test@example.com", "encoded-password", CredentialProvider.LOCAL, user);
    }

    public static TokenResult 토큰결과() {
        return new TokenResult("access-token", "refresh-token", Duration.ofMinutes(30));
    }

    public static JwtUserClaims JWT클레임(UUID userId, List<Role> roles) {
        return new JwtUserClaims(userId, roles);
    }
}
