package com.hanyoonsoo.ordersystem.api.auth.filter;

import com.hanyoonsoo.ordersystem.api.auth.custom.CustomAuthenticationEntryPoint;
import com.hanyoonsoo.ordersystem.api.auth.exception.JwtAuthenticationException;
import com.hanyoonsoo.ordersystem.application.auth.dto.JwtUserClaims;
import com.hanyoonsoo.ordersystem.application.auth.port.in.AuthServicePort;
import com.hanyoonsoo.ordersystem.application.auth.port.in.AuthTokenServicePort;
import com.hanyoonsoo.ordersystem.common.exception.ErrorCode;
import com.hanyoonsoo.ordersystem.common.exception.base.UnauthorizedException;
import com.hanyoonsoo.ordersystem.core.domain.user.entity.Role;
import jakarta.servlet.FilterChain;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatcher;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class JwtAuthenticationFilterTest {

    @Mock
    private AuthServicePort authService;
    @Mock
    private AuthTokenServicePort authTokenService;
    @Mock
    private CustomAuthenticationEntryPoint customAuthenticationEntryPoint;
    @InjectMocks
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void 허용된_경로는_필터를_건너뛴다() {
        // given
        MockHttpServletRequest request = new MockHttpServletRequest("POST", "/api/v1/auth/sign-in");
        request.setServletPath("/api/v1/auth/sign-in");

        // when
        boolean actual = jwtAuthenticationFilter.shouldNotFilter(request);

        // then
        assertThat(actual).isTrue();
    }

    @Test
    void Bearer_헤더가_없으면_다음_필터로_바로_전달한다() throws Exception {
        // given
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/v1/products/1");
        request.setServletPath("/api/v1/products/1");
        MockHttpServletResponse response = new MockHttpServletResponse();
        FilterChain filterChain = mock(FilterChain.class);

        // when
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // then
        then(filterChain).should().doFilter(request, response);
        then(authService).should(never()).validateAndExtractUserClaimsFromAccessToken(org.mockito.ArgumentMatchers.anyString());
    }

    @Test
    void 로그아웃된_액세스_토큰이면_엔트리포인트에_위임한다() throws Exception {
        // given
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/v1/products/1");
        request.setServletPath("/api/v1/products/1");
        request.addHeader(HttpHeaders.AUTHORIZATION, "Bearer access-token");
        MockHttpServletResponse response = new MockHttpServletResponse();
        FilterChain filterChain = mock(FilterChain.class);
        given(authTokenService.isLogoutAccessToken("access-token")).willReturn(true);

        // when
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // then
        then(customAuthenticationEntryPoint).should().commence(
                eq(request),
                eq(response),
                org.mockito.ArgumentMatchers.<AuthenticationException>argThat(jwtAuthenticationException(ErrorCode.LOGGED_OUT_ACCESS_TOKEN))
        );
        then(filterChain).should(never()).doFilter(request, response);
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }

    @Test
    void 유효한_토큰이면_인증정보를_저장하고_다음_필터로_전달한다() throws Exception {
        // given
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/v1/products/1");
        request.setServletPath("/api/v1/products/1");
        request.addHeader(HttpHeaders.AUTHORIZATION, "Bearer access-token");
        MockHttpServletResponse response = new MockHttpServletResponse();
        FilterChain filterChain = mock(FilterChain.class);
        JwtUserClaims claims = new JwtUserClaims(UUID.randomUUID(), List.of(Role.USER));
        given(authTokenService.isLogoutAccessToken("access-token")).willReturn(false);
        given(authService.validateAndExtractUserClaimsFromAccessToken("access-token")).willReturn(claims);

        // when
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // then
        then(filterChain).should().doFilter(request, response);
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNotNull();
        assertThat(SecurityContextHolder.getContext().getAuthentication().getPrincipal()).isEqualTo(claims);
        assertThat(SecurityContextHolder.getContext().getAuthentication().getAuthorities())
                .extracting(Object::toString)
                .containsExactly("ROLE_USER");
    }

    @Test
    void 토큰_검증중_인가_예외가_발생하면_JWT_예외로_변환해_위임한다() throws Exception {
        // given
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/v1/products/1");
        request.setServletPath("/api/v1/products/1");
        request.addHeader(HttpHeaders.AUTHORIZATION, "Bearer access-token");
        MockHttpServletResponse response = new MockHttpServletResponse();
        FilterChain filterChain = mock(FilterChain.class);
        given(authTokenService.isLogoutAccessToken("access-token")).willReturn(false);
        given(authService.validateAndExtractUserClaimsFromAccessToken("access-token"))
                .willThrow(new UnauthorizedException(ErrorCode.INVALID_TOKEN, "invalid"));

        // when
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // then
        then(customAuthenticationEntryPoint).should().commence(
                eq(request),
                eq(response),
                org.mockito.ArgumentMatchers.<AuthenticationException>argThat(jwtAuthenticationException(ErrorCode.INVALID_TOKEN))
        );
        then(filterChain).should(never()).doFilter(request, response);
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }

    @Test
    void 런타임_예외가_발생하면_부족한_인증_예외로_위임한다() throws Exception {
        // given
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/v1/products/1");
        request.setServletPath("/api/v1/products/1");
        request.addHeader(HttpHeaders.AUTHORIZATION, "Bearer access-token");
        MockHttpServletResponse response = new MockHttpServletResponse();
        FilterChain filterChain = mock(FilterChain.class);
        given(authTokenService.isLogoutAccessToken("access-token")).willReturn(false);
        given(authService.validateAndExtractUserClaimsFromAccessToken("access-token"))
                .willThrow(new IllegalStateException("boom"));

        // when
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // then
        then(customAuthenticationEntryPoint).should().commence(
                eq(request),
                eq(response),
                argThat(exception -> exception instanceof InsufficientAuthenticationException
                        && "boom".equals(exception.getMessage()))
        );
        then(filterChain).should(never()).doFilter(request, response);
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }

    private ArgumentMatcher<AuthenticationException> jwtAuthenticationException(ErrorCode errorCode) {
        return exception -> exception instanceof JwtAuthenticationException jwtAuthenticationException
                && jwtAuthenticationException.getErrorCode() == errorCode;
    }
}
