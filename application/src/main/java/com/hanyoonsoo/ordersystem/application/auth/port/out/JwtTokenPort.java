package com.hanyoonsoo.ordersystem.application.auth.port.out;

import com.hanyoonsoo.ordersystem.application.auth.dto.JwtUserClaims;
import com.hanyoonsoo.ordersystem.application.auth.dto.TokenResult;
import com.hanyoonsoo.ordersystem.core.domain.user.entity.Role;

import java.util.List;
import java.util.UUID;

public interface JwtTokenPort {

    TokenResult createTokens(UUID userId, List<Role> roles);

    JwtUserClaims validateAndExtractUserClaimsFromAccessToken(String token);

    JwtUserClaims validateAndExtractUserClaimsFromRefreshToken(String token);

    JwtUserClaims extractUserClaimsFromAccessTokenAllowExpired(String token);

    long getAccessTokenExpirationMillis();
}
