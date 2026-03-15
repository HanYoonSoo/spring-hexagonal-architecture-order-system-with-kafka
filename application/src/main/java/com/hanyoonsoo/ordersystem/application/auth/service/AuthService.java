package com.hanyoonsoo.ordersystem.application.auth.service;

import com.hanyoonsoo.ordersystem.application.auth.dto.JwtUserClaims;
import com.hanyoonsoo.ordersystem.application.auth.dto.SignInCommand;
import com.hanyoonsoo.ordersystem.application.auth.dto.TokenDto;
import com.hanyoonsoo.ordersystem.application.auth.port.in.AuthRedisServicePort;
import com.hanyoonsoo.ordersystem.application.auth.port.in.AuthServicePort;
import com.hanyoonsoo.ordersystem.application.auth.port.out.JwtTokenPort;
import com.hanyoonsoo.ordersystem.application.common.transaction.ReadOnlyTransactional;
import com.hanyoonsoo.ordersystem.application.user.model.EmailPasswordCredential;
import com.hanyoonsoo.ordersystem.application.user.port.out.UserRepository;
import com.hanyoonsoo.ordersystem.common.exception.ErrorCode;
import com.hanyoonsoo.ordersystem.common.exception.base.UnauthorizedException;
import com.hanyoonsoo.ordersystem.core.domain.user.entity.Role;
import com.hanyoonsoo.ordersystem.core.domain.user.entity.UserCredential;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class AuthService implements AuthServicePort {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenPort jwtTokenPort;
    private final AuthRedisServicePort authRedisService;

    @Override
    @ReadOnlyTransactional
    public TokenDto signIn(SignInCommand command) {
        EmailPasswordCredential credential = EmailPasswordCredential.from(command.credential());
        UserCredential userCredential = userRepository.findUserCredentialByLoginIdAndProvider(
                        credential.loginId(),
                        command.credentialProvider()
                )
                .orElseThrow(() -> new UnauthorizedException(ErrorCode.EMAIL_OR_PASSWORD_MISMATCH));

        boolean matched = passwordEncoder.matches(
                credential.loginId() + credential.plainPassword(),
                userCredential.getValue()
        );
        if (!matched) {
            throw new UnauthorizedException(ErrorCode.EMAIL_OR_PASSWORD_MISMATCH);
        }

        List<Role> userRoles = userRepository.findRolesByUserId(userCredential.getUser().getId());
        TokenDto tokenDto = jwtTokenPort.createTokens(userCredential.getUser().getId(), userRoles);
        authRedisService.saveRefreshToken(
                userCredential.getUser().getId().toString(),
                tokenDto.refreshToken(),
                tokenDto.refreshTokenExpiration().toMillis()
        );
        return tokenDto;
    }

    @Override
    @Transactional
    public TokenDto reissue(String accessToken, String refreshToken) {
        JwtUserClaims accessClaims = jwtTokenPort.extractUserClaimsFromAccessTokenAllowExpired(accessToken);
        JwtUserClaims refreshClaims = jwtTokenPort.validateAndExtractUserClaimsFromRefreshToken(refreshToken);

        if (!accessClaims.id().equals(refreshClaims.id())) {
            throw new UnauthorizedException(ErrorCode.INVALID_TOKEN);
        }

        authRedisService.matchRefreshTokenOrThrow(refreshClaims.id().toString(), refreshToken);

        List<Role> currentRoles = userRepository.findRolesByUserId(refreshClaims.id());
        if (!Set.copyOf(currentRoles).equals(Set.copyOf(refreshClaims.roles()))) {
            throw new UnauthorizedException(ErrorCode.ROLE_MISMATCH);
        }

        TokenDto reissued = jwtTokenPort.createTokens(refreshClaims.id(), currentRoles);
        authRedisService.saveRefreshToken(
                refreshClaims.id().toString(),
                reissued.refreshToken(),
                reissued.refreshTokenExpiration().toMillis()
        );
        return reissued;
    }

    @Override
    @Transactional
    public void logout(String userId, String accessToken) {
        authRedisService.deleteRefreshToken(userId);
        authRedisService.saveAccessTokenForLogout(accessToken, jwtTokenPort.getAccessTokenExpirationMillis());
    }
}
