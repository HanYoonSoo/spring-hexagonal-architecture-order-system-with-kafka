package com.hanyoonsoo.ordersystem.adapter.out.security.jwt;

import com.hanyoonsoo.ordersystem.adapter.out.security.config.JwtSecurityProperties;
import com.hanyoonsoo.ordersystem.application.auth.dto.JwtUserClaims;
import com.hanyoonsoo.ordersystem.application.auth.dto.TokenDto;
import com.hanyoonsoo.ordersystem.common.exception.ErrorCode;
import com.hanyoonsoo.ordersystem.common.exception.base.UnauthorizedException;
import com.hanyoonsoo.ordersystem.core.domain.user.entity.Role;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.Base64;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class JwtProvider {

    private static final String CLAIM_ROLES = "roles";

    private final JwtSecurityProperties jwtSecurityProperties;

    private SecretKey secretKey;

    @PostConstruct
    public void initSecretKey() {
        String secret = jwtSecurityProperties.getSecret();
        if (secret == null || secret.isBlank()) {
            throw new IllegalStateException("spring.security.jwt.secret must not be blank");
        }
        byte[] keyBytes = decodeSecret(secret);
        this.secretKey = Keys.hmacShaKeyFor(keyBytes);
    }

    public TokenDto createTokens(UUID userId, List<Role> roles) {
        long accessTokenExpirationSeconds = jwtSecurityProperties.getAccessTokenExpirationSeconds();
        long refreshTokenExpirationSeconds = jwtSecurityProperties.getRefreshTokenExpirationSeconds();
        String accessToken = createToken(userId, roles, accessTokenExpirationSeconds);
        String refreshToken = createToken(userId, roles, refreshTokenExpirationSeconds);
        return new TokenDto(accessToken, refreshToken, Duration.ofSeconds(refreshTokenExpirationSeconds));
    }

    public String createAccessToken(UUID userId, List<Role> roles) {
        return createToken(userId, roles, jwtSecurityProperties.getAccessTokenExpirationSeconds());
    }

    public JwtUserClaims validateAndExtractUserClaimsFromAccessToken(String token) {
        return validateAndExtractUserClaims(token);
    }

    public JwtUserClaims extractUserClaimsFromAccessTokenAllowExpired(String token) {
        return extractUserClaims(parseClaimsAllowExpired(token));
    }

    public JwtUserClaims validateAndExtractUserClaimsFromRefreshToken(String token) {
        return validateAndExtractUserClaims(token);
    }

    public long getAccessTokenExpirationMillis() {
        return jwtSecurityProperties.getAccessTokenExpirationSeconds() * 1000;
    }

    public long getRefreshTokenExpirationMillis() {
        return jwtSecurityProperties.getRefreshTokenExpirationSeconds() * 1000;
    }

    private JwtUserClaims validateAndExtractUserClaims(String token) {
        return extractUserClaims(parseClaims(token));
    }

    private JwtUserClaims extractUserClaims(Claims claims) {
        if (!jwtSecurityProperties.getIssuer().equals(claims.getIssuer())) {
            throw new UnauthorizedException(ErrorCode.INVALID_TOKEN);
        }

        String userId = claims.getSubject();
        if (userId == null || userId.isBlank()) {
            throw new UnauthorizedException(ErrorCode.INVALID_TOKEN);
        }

        UUID parsedUserId;
        try {
            parsedUserId = UUID.fromString(userId);
        } catch (IllegalArgumentException e) {
            throw new UnauthorizedException(ErrorCode.INVALID_TOKEN, ErrorCode.INVALID_TOKEN.message("invalid subject"));
        }

        return new JwtUserClaims(parsedUserId, extractRoles(claims.get(CLAIM_ROLES)));
    }

    private String createToken(
            UUID userId,
            List<Role> roles,
            long expirationSeconds
    ) {
        Instant now = Instant.now();
        Instant expiresAt = now.plusSeconds(expirationSeconds);

        return Jwts.builder()
                .setSubject(userId.toString())
                .setIssuer(jwtSecurityProperties.getIssuer())
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(expiresAt))
                .claim(CLAIM_ROLES, normalizeRoles(roles))
                .signWith(secretKey)
                .compact();
    }

    private Claims parseClaims(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (ExpiredJwtException e) {
            throw new UnauthorizedException(ErrorCode.ACCESS_TOKEN_EXPIRED);
        } catch (UnsupportedJwtException | MalformedJwtException | SignatureException | IllegalArgumentException e) {
            throw new UnauthorizedException(ErrorCode.INVALID_TOKEN);
        } catch (Exception e) {
            throw new UnauthorizedException(ErrorCode.INVALID_TOKEN, ErrorCode.INVALID_TOKEN.message("token parse failed"));
        }
    }

    private Claims parseClaimsAllowExpired(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (ExpiredJwtException e) {
            return e.getClaims();
        } catch (UnsupportedJwtException | MalformedJwtException | SignatureException | IllegalArgumentException e) {
            throw new UnauthorizedException(ErrorCode.INVALID_TOKEN);
        } catch (Exception e) {
            throw new UnauthorizedException(ErrorCode.INVALID_TOKEN, ErrorCode.INVALID_TOKEN.message("token parse failed"));
        }
    }

    private static List<Role> extractRoles(Object claim) {
        if (claim instanceof Collection<?> collection) {
            return collection.stream()
                    .map(String::valueOf)
                    .filter(role -> !role.isBlank())
                    .map(JwtProvider::toRole)
                    .toList();
        }
        return List.of();
    }

    private static List<String> normalizeRoles(List<Role> roles) {
        if (roles == null) {
            return List.of();
        }
        return roles.stream()
                .map(Role::name)
                .toList();
    }

    private static Role toRole(String roleName) {
        try {
            return Role.valueOf(roleName);
        } catch (IllegalArgumentException e) {
            throw new UnauthorizedException(ErrorCode.INVALID_TOKEN, ErrorCode.INVALID_TOKEN.message("invalid role: " + roleName));
        }
    }

    private static byte[] decodeSecret(String secret) {
        try {
            return Base64.getDecoder().decode(secret);
        } catch (IllegalArgumentException ignored) {
            return secret.getBytes(StandardCharsets.UTF_8);
        }
    }
}
