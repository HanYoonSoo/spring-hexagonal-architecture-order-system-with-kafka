package com.hanyoonsoo.ordersystem.api.auth.utils;

import com.hanyoonsoo.ordersystem.api.auth.config.CookieProperties;
import com.hanyoonsoo.ordersystem.application.auth.dto.TokenResult;
import com.hanyoonsoo.ordersystem.application.auth.port.in.AuthServicePort;
import com.hanyoonsoo.ordersystem.common.exception.base.UnauthorizedException;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.Test;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AuthCookieUtilsTest {

    @Test
    void 리프레시_토큰_쿠키를_생성한다() {
        // given
        CookieProperties cookieProperties = new CookieProperties();
        cookieProperties.setSameSite("Lax");
        cookieProperties.setSecure(true);
        TokenResult tokenResult = new TokenResult("access", "refresh", Duration.ofDays(7));

        // when
        String actual = AuthCookieUtils.buildRefreshTokenCookie(tokenResult, cookieProperties).toString();

        // then
        assertThat(actual).contains("s_rt=refresh");
        assertThat(actual).contains("HttpOnly");
        assertThat(actual).contains("Secure");
        assertThat(actual).contains("SameSite=Lax");
    }

    @Test
    void 쿠키에서_리프레시_토큰을_추출한다() {
        // given
        Cookie[] cookies = {
                new Cookie("other", "value"),
                new Cookie(AuthServicePort.REFRESH_TOKEN_COOKIE_NAME, "refresh-token")
        };

        // when
        String actual = AuthCookieUtils.extractRefreshToken(cookies);

        // then
        assertThat(actual).isEqualTo("refresh-token");
    }

    @Test
    void 리프레시_토큰_쿠키가_없으면_예외가_발생한다() {
        // given
        Cookie[] cookies = {new Cookie("other", "value")};

        // when & then
        assertThatThrownBy(() -> AuthCookieUtils.extractRefreshToken(cookies))
                .isInstanceOf(UnauthorizedException.class);
    }
}
