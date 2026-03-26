package com.hanyoonsoo.ordersystem.adapter.out.security.jwt;

import com.hanyoonsoo.ordersystem.application.auth.dto.JwtUserClaims;
import com.hanyoonsoo.ordersystem.application.auth.dto.TokenResult;
import com.hanyoonsoo.ordersystem.core.domain.user.entity.Role;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Duration;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class JwtTokenProviderAdapterTest {

    @Mock
    private JwtProvider jwtProvider;
    @InjectMocks
    private JwtTokenProviderAdapter jwtTokenProviderAdapter;

    @Test
    void 토큰_생성을_위임한다() {
        // given
        UUID userId = UUID.randomUUID();
        TokenResult tokenResult = new TokenResult("access", "refresh", Duration.ofMinutes(30));
        given(jwtProvider.createTokens(userId, List.of(Role.USER))).willReturn(tokenResult);

        // when
        TokenResult actual = jwtTokenProviderAdapter.createTokens(userId, List.of(Role.USER));

        // then
        assertThat(actual).isEqualTo(tokenResult);
    }

    @Test
    void 액세스_토큰_검증과_클레임_추출을_위임한다() {
        // given
        JwtUserClaims claims = new JwtUserClaims(UUID.randomUUID(), List.of(Role.USER));
        given(jwtProvider.validateAndExtractUserClaimsFromAccessToken("access")).willReturn(claims);

        // when
        JwtUserClaims actual = jwtTokenProviderAdapter.validateAndExtractUserClaimsFromAccessToken("access");

        // then
        assertThat(actual).isEqualTo(claims);
    }

    @Test
    void 리프레시_토큰_검증과_클레임_추출을_위임한다() {
        // given
        JwtUserClaims claims = new JwtUserClaims(UUID.randomUUID(), List.of(Role.USER));
        given(jwtProvider.validateAndExtractUserClaimsFromRefreshToken("refresh")).willReturn(claims);

        // when
        JwtUserClaims actual = jwtTokenProviderAdapter.validateAndExtractUserClaimsFromRefreshToken("refresh");

        // then
        assertThat(actual).isEqualTo(claims);
    }

    @Test
    void 만료된_액세스_토큰_클레임_추출을_위임한다() {
        // given
        JwtUserClaims claims = new JwtUserClaims(UUID.randomUUID(), List.of(Role.USER));
        given(jwtProvider.extractUserClaimsFromAccessTokenAllowExpired("access")).willReturn(claims);

        // when
        JwtUserClaims actual = jwtTokenProviderAdapter.extractUserClaimsFromAccessTokenAllowExpired("access");

        // then
        assertThat(actual).isEqualTo(claims);
    }

    @Test
    void 액세스_토큰_만료시간_조회을_위임한다() {
        // given
        given(jwtProvider.getAccessTokenExpirationMillis()).willReturn(1000L);

        // when
        long actual = jwtTokenProviderAdapter.getAccessTokenExpirationMillis();

        // then
        assertThat(actual).isEqualTo(1000L);
        then(jwtProvider).should().getAccessTokenExpirationMillis();
    }
}
