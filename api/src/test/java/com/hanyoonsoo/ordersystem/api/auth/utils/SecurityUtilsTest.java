package com.hanyoonsoo.ordersystem.api.auth.utils;

import com.hanyoonsoo.ordersystem.application.auth.dto.JwtUserClaims;
import com.hanyoonsoo.ordersystem.common.exception.base.UnauthorizedException;
import com.hanyoonsoo.ordersystem.core.domain.user.entity.Role;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SecurityUtilsTest {

    @AfterEach
    void tearDown() {
        SecurityUtils.clear();
    }

    @Test
    void 인증된_사용자_ID를_조회한다() {
        // given
        UUID userId = UUID.randomUUID();
        JwtUserClaims claims = new JwtUserClaims(userId, List.of(Role.USER));
        SecurityContextHolder.getContext().setAuthentication(new PreAuthenticatedAuthenticationToken(claims, null));

        // when
        UUID actual = SecurityUtils.requiredAuthenticatedUserId();

        // then
        assertThat(actual).isEqualTo(userId);
    }

    @Test
    void 인증_정보가_없으면_예외가_발생한다() {
        // given

        // when & then
        assertThatThrownBy(SecurityUtils::requiredAuthenticatedUserId)
                .isInstanceOf(UnauthorizedException.class);
    }
}
