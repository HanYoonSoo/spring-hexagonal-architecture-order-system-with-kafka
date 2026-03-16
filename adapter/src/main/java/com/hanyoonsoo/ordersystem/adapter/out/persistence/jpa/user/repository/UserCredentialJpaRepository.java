package com.hanyoonsoo.ordersystem.adapter.out.persistence.jpa.user.repository;

import com.hanyoonsoo.ordersystem.core.domain.user.entity.CredentialProvider;
import com.hanyoonsoo.ordersystem.core.domain.user.entity.UserCredential;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserCredentialJpaRepository extends JpaRepository<UserCredential, Long> {

    boolean existsUserCredentialByLoginIdAndDeletedAtIsNull(String loginId);

    Optional<UserCredential> findUserCredentialByLoginIdAndProviderAndDeletedAtIsNull(String loginId, CredentialProvider provider);
}
