package com.hanyoonsoo.ordersystem.api.user.dto.response;

import com.hanyoonsoo.ordersystem.application.user.dto.UserInfoDto;
import com.hanyoonsoo.ordersystem.core.domain.user.entity.Role;

import java.util.List;
import java.util.UUID;

public record UserInfoResponse(
        UUID id,
        String name,
        List<Role> roles
) {
    public static UserInfoResponse from(UserInfoDto dto) {
        return new UserInfoResponse(dto.id(), dto.name(), dto.roles());
    }
}
