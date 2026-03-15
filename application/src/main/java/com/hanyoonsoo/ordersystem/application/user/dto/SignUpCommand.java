package com.hanyoonsoo.ordersystem.application.user.dto;

import com.hanyoonsoo.ordersystem.core.domain.user.entity.CredentialProvider;
import com.hanyoonsoo.ordersystem.core.domain.user.entity.Role;

public record SignUpCommand(
        String name,
        String credential,
        CredentialProvider credentialProvider,
        Role userRole
) {
}
