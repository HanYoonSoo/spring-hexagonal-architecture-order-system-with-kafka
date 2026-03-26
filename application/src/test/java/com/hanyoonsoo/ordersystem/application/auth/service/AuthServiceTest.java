package com.hanyoonsoo.ordersystem.application.auth.service;

import com.hanyoonsoo.ordersystem.application.auth.dto.JwtUserClaims;
import com.hanyoonsoo.ordersystem.application.auth.dto.SignInCommand;
import com.hanyoonsoo.ordersystem.application.auth.dto.TokenResult;
import com.hanyoonsoo.ordersystem.application.auth.port.in.AuthTokenServicePort;
import com.hanyoonsoo.ordersystem.application.auth.port.out.JwtTokenProvider;
import com.hanyoonsoo.ordersystem.application.support.fixture.UserFixture;
import com.hanyoonsoo.ordersystem.application.user.port.out.UserRepository;
import com.hanyoonsoo.ordersystem.common.exception.base.UnauthorizedException;
import com.hanyoonsoo.ordersystem.core.domain.user.entity.Role;
import com.hanyoonsoo.ordersystem.core.domain.user.entity.User;
import com.hanyoonsoo.ordersystem.core.domain.user.entity.UserCredential;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private JwtTokenProvider jwtTokenProvider;
    @Mock
    private AuthTokenServicePort authTokenService;
    @InjectMocks
    private AuthService authService;

    @Test
    void 로그인시_인증정보가_존재하지_않으면_예외가_발생한다() {
        // given
        SignInCommand command = UserFixture.로그인명령();
        given(userRepository.findUserCredentialByLoginIdAndProvider("test@example.com", command.credentialProvider()))
                .willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> authService.signIn(command))
                .isInstanceOf(UnauthorizedException.class);
    }

    @Test
    void 로그인시_비밀번호가_일치하지_않으면_예외가_발생한다() {
        // given
        SignInCommand command = UserFixture.로그인명령();
        UserCredential userCredential = UserFixture.인증정보(UserFixture.사용자());
        given(userRepository.findUserCredentialByLoginIdAndProvider("test@example.com", command.credentialProvider()))
                .willReturn(Optional.of(userCredential));
        given(passwordEncoder.matches("test@example.compassword12", userCredential.getValue())).willReturn(false);

        // when & then
        assertThatThrownBy(() -> authService.signIn(command))
                .isInstanceOf(UnauthorizedException.class);
    }

    @Test
    void 로그인에_성공하면_토큰을_반환하고_리프레시_토큰을_저장한다() {
        // given
        SignInCommand command = UserFixture.로그인명령();
        User user = UserFixture.사용자();
        UserCredential userCredential = UserFixture.인증정보(user);
        TokenResult tokenResult = UserFixture.토큰결과();
        given(userRepository.findUserCredentialByLoginIdAndProvider("test@example.com", command.credentialProvider()))
                .willReturn(Optional.of(userCredential));
        given(passwordEncoder.matches("test@example.compassword12", userCredential.getValue())).willReturn(true);
        given(userRepository.findRolesByUserId(userCredential.getUser().getId())).willReturn(List.of(Role.USER));
        given(jwtTokenProvider.createTokens(userCredential.getUser().getId(), List.of(Role.USER))).willReturn(tokenResult);

        // when
        TokenResult actual = authService.signIn(command);

        // then
        assertThat(actual).isEqualTo(tokenResult);
        then(authTokenService).should().saveRefreshToken(
                userCredential.getUser().getId().toString(),
                tokenResult.refreshToken(),
                tokenResult.refreshTokenExpiration().toMillis()
        );
    }

    @Test
    void 사용자_클레임_검증과_추출을_프로바이더에_위임한다() {
        // given
        JwtUserClaims claims = UserFixture.JWT클레임(UUID.randomUUID(), List.of(Role.USER));
        given(jwtTokenProvider.validateAndExtractUserClaimsFromAccessToken("access-token")).willReturn(claims);

        // when
        JwtUserClaims actual = authService.validateAndExtractUserClaimsFromAccessToken("access-token");

        // then
        assertThat(actual).isEqualTo(claims);
    }

    @Test
    void 재발급시_액세스_토큰과_리프레시_토큰의_소유자가_다르면_예외가_발생한다() {
        // given
        JwtUserClaims accessClaims = UserFixture.JWT클레임(UUID.randomUUID(), List.of(Role.USER));
        JwtUserClaims refreshClaims = UserFixture.JWT클레임(UUID.randomUUID(), List.of(Role.USER));
        given(jwtTokenProvider.extractUserClaimsFromAccessTokenAllowExpired("access-token")).willReturn(accessClaims);
        given(jwtTokenProvider.validateAndExtractUserClaimsFromRefreshToken("refresh-token")).willReturn(refreshClaims);

        // when & then
        assertThatThrownBy(() -> authService.reissue("access-token", "refresh-token"))
                .isInstanceOf(UnauthorizedException.class);
    }

    @Test
    void 재발급시_현재_권한과_토큰_권한이_다르면_예외가_발생한다() {
        // given
        UUID userId = UUID.randomUUID();
        JwtUserClaims accessClaims = UserFixture.JWT클레임(userId, List.of(Role.USER));
        JwtUserClaims refreshClaims = UserFixture.JWT클레임(userId, List.of(Role.USER));
        given(jwtTokenProvider.extractUserClaimsFromAccessTokenAllowExpired("access-token")).willReturn(accessClaims);
        given(jwtTokenProvider.validateAndExtractUserClaimsFromRefreshToken("refresh-token")).willReturn(refreshClaims);
        given(userRepository.findRolesByUserId(userId)).willReturn(List.of(Role.ADMIN));

        // when & then
        assertThatThrownBy(() -> authService.reissue("access-token", "refresh-token"))
                .isInstanceOf(UnauthorizedException.class);
    }

    @Test
    void 재발급에_성공하면_새_토큰을_생성하고_리프레시_토큰을_저장한다() {
        // given
        UUID userId = UUID.randomUUID();
        JwtUserClaims accessClaims = UserFixture.JWT클레임(userId, List.of(Role.USER));
        JwtUserClaims refreshClaims = UserFixture.JWT클레임(userId, List.of(Role.USER));
        TokenResult tokenResult = UserFixture.토큰결과();
        given(jwtTokenProvider.extractUserClaimsFromAccessTokenAllowExpired("access-token")).willReturn(accessClaims);
        given(jwtTokenProvider.validateAndExtractUserClaimsFromRefreshToken("refresh-token")).willReturn(refreshClaims);
        given(userRepository.findRolesByUserId(userId)).willReturn(List.of(Role.USER));
        given(jwtTokenProvider.createTokens(userId, List.of(Role.USER))).willReturn(tokenResult);

        // when
        TokenResult actual = authService.reissue("access-token", "refresh-token");

        // then
        assertThat(actual).isEqualTo(tokenResult);
        then(authTokenService).should().matchRefreshTokenOrThrow(userId.toString(), "refresh-token");
        then(authTokenService).should().saveRefreshToken(userId.toString(), tokenResult.refreshToken(), tokenResult.refreshTokenExpiration().toMillis());
    }

    @Test
    void 로그아웃시_리프레시_토큰을_삭제하고_로그아웃_액세스_토큰을_저장한다() {
        // given
        given(jwtTokenProvider.getAccessTokenExpirationMillis()).willReturn(1000L);

        // when
        authService.logout("user-1", "access-token");

        // then
        then(authTokenService).should().deleteRefreshToken("user-1");
        then(authTokenService).should().saveAccessTokenForLogout("access-token", 1000L);
    }
}
