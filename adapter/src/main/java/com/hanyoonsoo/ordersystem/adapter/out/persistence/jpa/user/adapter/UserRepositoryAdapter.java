package com.hanyoonsoo.ordersystem.adapter.out.persistence.jpa.user.adapter;

import com.hanyoonsoo.ordersystem.adapter.out.persistence.jpa.user.repository.UserCredentialJpaRepository;
import com.hanyoonsoo.ordersystem.adapter.out.persistence.jpa.user.repository.UserJpaRepository;
import com.hanyoonsoo.ordersystem.adapter.out.persistence.jpa.user.repository.UserRoleJpaRepository;
import com.hanyoonsoo.ordersystem.application.user.port.out.UserRepository;
import com.hanyoonsoo.ordersystem.core.domain.user.entity.CredentialProvider;
import com.hanyoonsoo.ordersystem.core.domain.user.entity.Role;
import com.hanyoonsoo.ordersystem.core.domain.user.entity.User;
import com.hanyoonsoo.ordersystem.core.domain.user.entity.UserCredential;
import com.hanyoonsoo.ordersystem.core.domain.user.entity.UserRole;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class UserRepositoryAdapter implements UserRepository {

    private final UserJpaRepository userJpaRepository;
    private final UserCredentialJpaRepository userCredentialJpaRepository;
    private final UserRoleJpaRepository userRoleJpaRepository;

    @Override
    public User save(User user) {
        return userJpaRepository.save(user);
    }

    @Override
    public void saveUserRole(UserRole userRole) {
        userRoleJpaRepository.save(userRole);
    }

    @Override
    public void saveUserCredential(UserCredential userCredential) {
        userCredentialJpaRepository.save(userCredential);
    }

    @Override
    public boolean existsByLoginId(String loginId) {
        return userCredentialJpaRepository.existsUserCredentialByLoginIdAndDeletedAtIsNull(loginId);
    }

    @Override
    public Optional<User> findById(UUID userId) {
        return userJpaRepository.findUserByIdAndDeletedAtIsNull(userId);
    }

    @Override
    public Optional<UserCredential> findUserCredentialByLoginIdAndProvider(String loginId, CredentialProvider credentialProvider) {
        return userCredentialJpaRepository.findUserCredentialByLoginIdAndProviderAndDeletedAtIsNull(loginId, credentialProvider);
    }

    @Override
    public List<Role> findRolesByUserId(UUID userId) {
        return userRoleJpaRepository.findUserRolesByUserId(userId).stream()
                .map(UserRole::getRole)
                .toList();
    }
}
