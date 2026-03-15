package com.hanyoonsoo.ordersystem.adapter.out.persistence.jpa.user.repository;

import com.hanyoonsoo.ordersystem.core.domain.user.entity.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface UserRoleJpaRepository extends JpaRepository<UserRole, Long> {

    List<UserRole> findByUserId(UUID userId);
}
