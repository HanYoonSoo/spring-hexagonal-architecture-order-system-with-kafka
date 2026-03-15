package com.hanyoonsoo.ordersystem.application.auth.dto;

import com.hanyoonsoo.ordersystem.core.domain.user.entity.Role;

import java.util.List;
import java.util.UUID;

public record JwtUserClaims(UUID id, List<Role> roles) {

    public JwtUserClaims {
        roles = roles == null ? List.of() : List.copyOf(roles);
    }
}
