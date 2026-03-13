package com.hanyoonsoo.ordersystem.api.auth.service;

import com.hanyoonsoo.ordersystem.api.auth.jwt.JwtProvider;
import com.hanyoonsoo.ordersystem.api.auth.jwt.JwtUserClaims;
import com.hanyoonsoo.ordersystem.api.auth.jwt.TokenDto;
import com.hanyoonsoo.ordersystem.core.domain.user.entity.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthTokenService {

    private final JwtProvider jwtProvider;
    // private final AuthRedisService authRedisService;

    public TokenDto issueTokens(UUID userId, List<Role> roles) {
        TokenDto tokenDto = jwtProvider.createTokens(userId, roles);
        // authRedisService.saveRefreshToken(userId.toString(), tokenDto.refreshToken(), tokenDto.refreshTokenExpiration().toMillis());
        return tokenDto;
    }

    public TokenDto reissueTokens(String refreshToken) {
        JwtUserClaims claims = jwtProvider.validateAndExtractUserClaimsFromRefreshToken(refreshToken);
        // authRedisService.matchRefreshTokenOrThrow(claims.id().toString(), refreshToken);
        TokenDto reissued = jwtProvider.createTokens(claims.id(), claims.roles());
        // authRedisService.saveRefreshToken(claims.id().toString(), reissued.refreshToken(), reissued.refreshTokenExpiration().toMillis());
        return reissued;
    }

    public void logout(UUID userId, String accessToken) {
        // authRedisService.deleteRefreshToken(userId.toString());
        // authRedisService.saveAccessTokenForLogout(accessToken, jwtProvider.accessTokenExpirationMillis());
    }
}
