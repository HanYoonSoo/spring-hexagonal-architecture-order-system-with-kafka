package com.hanyoonsoo.ordersystem.adapter.out.security.jwt;

import com.hanyoonsoo.ordersystem.application.auth.dto.JwtUserClaims;
import com.hanyoonsoo.ordersystem.application.auth.dto.TokenResult;
import com.hanyoonsoo.ordersystem.application.auth.port.out.JwtTokenProvider;
import com.hanyoonsoo.ordersystem.core.domain.user.entity.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class JwtTokenProviderAdapter implements JwtTokenProvider {

    private final JwtProvider jwtProvider;

    @Override
    public TokenResult createTokens(UUID userId, List<Role> roles) {
        return jwtProvider.createTokens(userId, roles);
    }

    @Override
    public JwtUserClaims validateAndExtractUserClaimsFromAccessToken(String token) {
        return jwtProvider.validateAndExtractUserClaimsFromAccessToken(token);
    }

    @Override
    public JwtUserClaims validateAndExtractUserClaimsFromRefreshToken(String token) {
        return jwtProvider.validateAndExtractUserClaimsFromRefreshToken(token);
    }

    @Override
    public JwtUserClaims extractUserClaimsFromAccessTokenAllowExpired(String token) {
        return jwtProvider.extractUserClaimsFromAccessTokenAllowExpired(token);
    }

    @Override
    public long getAccessTokenExpirationMillis() {
        return jwtProvider.getAccessTokenExpirationMillis();
    }
}
