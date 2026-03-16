package com.hanyoonsoo.ordersystem.adapter.out.persistence.jpa.user.repository;

import com.hanyoonsoo.ordersystem.core.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserJpaRepository extends JpaRepository<User, UUID> {

    Optional<User> findUserByIdAndDeletedAtIsNull(UUID id);
}
