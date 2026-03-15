package com.hanyoonsoo.ordersystem.application.user.service;

import com.hanyoonsoo.ordersystem.application.user.dto.SignUpCommand;
import com.hanyoonsoo.ordersystem.application.user.dto.UserInfoDto;
import com.hanyoonsoo.ordersystem.application.user.model.EmailPasswordCredential;
import com.hanyoonsoo.ordersystem.application.common.transaction.ReadOnlyTransactional;
import com.hanyoonsoo.ordersystem.application.user.port.in.UserServicePort;
import com.hanyoonsoo.ordersystem.application.user.port.out.UserRepository;
import com.hanyoonsoo.ordersystem.common.exception.ErrorCode;
import com.hanyoonsoo.ordersystem.common.exception.base.ConflictException;
import com.hanyoonsoo.ordersystem.common.exception.base.NotFoundException;
import com.hanyoonsoo.ordersystem.core.domain.user.entity.CredentialProvider;
import com.hanyoonsoo.ordersystem.core.domain.user.entity.Role;
import com.hanyoonsoo.ordersystem.core.domain.user.entity.User;
import com.hanyoonsoo.ordersystem.core.domain.user.entity.UserCredential;
import com.hanyoonsoo.ordersystem.core.domain.user.entity.UserRole;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService implements UserServicePort {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void signUp(SignUpCommand command) {
        EmailPasswordCredential emailPasswordCredential = EmailPasswordCredential.from(command.credential());
        throwIfExistsUserCredential(emailPasswordCredential.loginId());

        User user = User.from(command.name());
        userRepository.save(user);

        saveUserRelatedEntity(
                emailPasswordCredential,
                user,
                command.userRole(),
                command.credentialProvider()
        );
    }

    @Override
    @ReadOnlyTransactional
    public UserInfoDto getMyInfo(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.USER_NOT_FOUND));

        return new UserInfoDto(
                user.getId(),
                user.getName(),
                userRepository.findRolesByUserId(userId)
        );
    }

    private void throwIfExistsUserCredential(String loginId) {
        if (userRepository.existsByLoginId(loginId)) {
            throw new ConflictException(ErrorCode.DUPLICATE_USER_CREDENTIAL);
        }
    }

    private void saveUserRelatedEntity(
            EmailPasswordCredential emailPasswordCredential,
            User user,
            Role userRole,
            CredentialProvider credentialProvider
    ) {
        userRepository.saveUserRole(UserRole.of(userRole, user));
        userRepository.saveUserCredential(
                UserCredential.of(
                        emailPasswordCredential.loginId(),
                        passwordEncoder.encode(emailPasswordCredential.loginId() + emailPasswordCredential.plainPassword()),
                        credentialProvider,
                        user
                )
        );
    }
}
