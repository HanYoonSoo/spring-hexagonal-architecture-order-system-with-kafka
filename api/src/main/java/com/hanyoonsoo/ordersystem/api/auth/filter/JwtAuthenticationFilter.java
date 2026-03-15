package com.hanyoonsoo.ordersystem.api.auth.filter;

import com.hanyoonsoo.ordersystem.api.auth.exception.JwtAuthenticationException;
import com.hanyoonsoo.ordersystem.api.auth.config.AllowedPaths;
import com.hanyoonsoo.ordersystem.api.auth.custom.CustomAuthenticationEntryPoint;
import com.hanyoonsoo.ordersystem.adapter.out.security.jwt.JwtProvider;
import com.hanyoonsoo.ordersystem.application.auth.dto.JwtUserClaims;
import com.hanyoonsoo.ordersystem.application.auth.port.in.AuthRedisServicePort;
import com.hanyoonsoo.ordersystem.common.exception.ErrorCode;
import com.hanyoonsoo.ordersystem.common.exception.base.UnauthorizedException;
import com.hanyoonsoo.ordersystem.core.domain.user.entity.Role;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();
    private static final String BEARER_PREFIX = "Bearer ";

    private final JwtProvider jwtProvider;
    private final AuthRedisServicePort authRedisService;
    private final CustomAuthenticationEntryPoint customAuthenticationEntryPoint;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }

        String requestPath = request.getServletPath();
        return Arrays.stream(AllowedPaths.allowedPaths())
                .anyMatch(path -> PATH_MATCHER.match(path, requestPath));
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        String authorization = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (authorization == null || !authorization.startsWith(BEARER_PREFIX)) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = authorization.substring(BEARER_PREFIX.length());

        try {
            if (authRedisService.isLogoutAccessToken(token)) {
                throw new JwtAuthenticationException(ErrorCode.LOGGED_OUT_ACCESS_TOKEN);
            }
            JwtUserClaims claims = jwtProvider.validateAndExtractUserClaimsFromAccessToken(token);

            var authorities = claims.roles().stream()
                    .map(Role::toSpringRole)
                    .map(SimpleGrantedAuthority::new)
                    .toList();

            var authentication = new PreAuthenticatedAuthenticationToken(claims, token, authorities);
            SecurityContextHolder.getContext().setAuthentication(authentication);
        } catch (JwtAuthenticationException e) {
            SecurityContextHolder.clearContext();
            customAuthenticationEntryPoint.commence(request, response, e);
            return;
        } catch (UnauthorizedException e) {
            SecurityContextHolder.clearContext();
            customAuthenticationEntryPoint.commence(
                    request,
                    response,
                    new JwtAuthenticationException(e.getErrorCode(), e.getMessage(), e)
            );
            return;
        } catch (RuntimeException e) {
            SecurityContextHolder.clearContext();
            customAuthenticationEntryPoint.commence(
                    request,
                    response,
                    new InsufficientAuthenticationException(e.getMessage(), e)
            );
            return;
        }

        filterChain.doFilter(request, response);
    }
}
