package com.hanyoonsoo.ordersystem.application.auth.service;

import com.hanyoonsoo.ordersystem.application.auth.port.out.AuthTokenStore;
import com.hanyoonsoo.ordersystem.common.exception.base.UnauthorizedException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class AuthTokenServiceTest {

    @Mock
    private AuthTokenStore authTokenStore;
    @InjectMocks
    private AuthTokenService authTokenService;

    @Test
    void 리프레시_토큰_저장은_사용자_범위의_키를_사용한다() {
        // given
        String userId = "user-1";

        // when
        authTokenService.saveRefreshToken(userId, "refresh-token", 1000L);

        // then
        then(authTokenStore).should().saveRefreshToken("user:user-1:refreshToken", "refresh-token", 1000L);
    }

    @Test
    void 리프레시_토큰이_없으면_예외가_발생한다() {
        // given
        given(authTokenStore.findRefreshToken("user:user-1:refreshToken")).willReturn(null);

        // when & then
        assertThatThrownBy(() -> authTokenService.matchRefreshTokenOrThrow("user-1", "refresh-token"))
                .isInstanceOf(UnauthorizedException.class);
    }

    @Test
    void 리프레시_토큰이_일치하지_않으면_예외가_발생한다() {
        // given
        given(authTokenStore.findRefreshToken("user:user-1:refreshToken")).willReturn("other-token");

        // when & then
        assertThatThrownBy(() -> authTokenService.matchRefreshTokenOrThrow("user-1", "refresh-token"))
                .isInstanceOf(UnauthorizedException.class);
    }

    @Test
    void 리프레시_토큰이_일치하면_검증에_성공한다() {
        // given
        given(authTokenStore.findRefreshToken("user:user-1:refreshToken")).willReturn("refresh-token");

        // when
        authTokenService.matchRefreshTokenOrThrow("user-1", "refresh-token");

        // then
        then(authTokenStore).should().findRefreshToken("user:user-1:refreshToken");
    }

    @Test
    void 로그아웃_액세스_토큰_여부는_저장소를_조회한다() {
        // given
        given(authTokenStore.isLogoutAccessToken("access-token")).willReturn(true);

        // when
        boolean actual = authTokenService.isLogoutAccessToken("access-token");

        // then
        assertThat(actual).isTrue();
    }
}
