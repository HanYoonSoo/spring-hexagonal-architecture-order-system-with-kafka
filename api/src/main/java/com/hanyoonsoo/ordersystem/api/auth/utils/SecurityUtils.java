package com.hanyoonsoo.ordersystem.api.auth.utils;

import com.hanyoonsoo.ordersystem.api.auth.jwt.JwtUserClaims;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;

import java.util.Optional;
import java.util.UUID;

public final class SecurityUtils {

    private SecurityUtils() {
    }

    public static Optional<UUID> findAuthenticatedUserId() {
        if (!(SecurityContextHolder.getContext().getAuthentication() instanceof PreAuthenticatedAuthenticationToken auth)) {
            return Optional.empty();
        }

        if (!(auth.getPrincipal() instanceof JwtUserClaims claims)) {
            return Optional.empty();
        }

        return Optional.ofNullable(claims.id());
    }

    public static UUID requiredAuthenticatedUserId() {
        return findAuthenticatedUserId()
                .orElseThrow(() -> new IllegalStateException("No authenticated user in security context"));
    }

    public static void clear() {
        SecurityContextHolder.clearContext();
    }
}
