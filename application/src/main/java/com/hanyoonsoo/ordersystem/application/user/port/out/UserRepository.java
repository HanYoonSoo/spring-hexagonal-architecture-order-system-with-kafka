package com.hanyoonsoo.ordersystem.application.user.port.out;

import com.hanyoonsoo.ordersystem.core.domain.user.entity.CredentialProvider;
import com.hanyoonsoo.ordersystem.core.domain.user.entity.Role;
import com.hanyoonsoo.ordersystem.core.domain.user.entity.User;
import com.hanyoonsoo.ordersystem.core.domain.user.entity.UserCredential;
import com.hanyoonsoo.ordersystem.core.domain.user.entity.UserRole;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository {

    User save(User user);

    void saveUserRole(UserRole userRole);

    void saveUserCredential(UserCredential userCredential);

    boolean existsByLoginId(String loginId);

    Optional<User> findById(UUID userId);

    Optional<UserCredential> findUserCredentialByLoginIdAndProvider(String loginId, CredentialProvider credentialProvider);

    List<Role> findRolesByUserId(UUID userId);
}
