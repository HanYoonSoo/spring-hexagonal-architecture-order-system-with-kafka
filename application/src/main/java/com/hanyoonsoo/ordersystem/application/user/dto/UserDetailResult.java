package com.hanyoonsoo.ordersystem.application.user.dto;

import com.hanyoonsoo.ordersystem.core.domain.user.entity.Role;

import java.util.List;
import java.util.UUID;

public record UserDetailResult(
        UUID id,
        String name,
        List<Role> roles
) {
}
