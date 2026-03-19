package com.hanyoonsoo.ordersystem.api.user.dto.response;

import com.hanyoonsoo.ordersystem.application.user.dto.UserDetailResult;
import com.hanyoonsoo.ordersystem.core.domain.user.entity.Role;

import java.util.List;
import java.util.UUID;

public record UserDetailResponse(
        UUID id,
        String name,
        List<Role> roles
) {
    public static UserDetailResponse from(UserDetailResult dto) {
        return new UserDetailResponse(dto.id(), dto.name(), dto.roles());
    }
}
