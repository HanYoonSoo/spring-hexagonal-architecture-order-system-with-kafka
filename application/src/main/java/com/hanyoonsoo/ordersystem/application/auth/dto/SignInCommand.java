package com.hanyoonsoo.ordersystem.application.auth.dto;

import com.hanyoonsoo.ordersystem.core.domain.user.entity.CredentialProvider;

public record SignInCommand(
        String credential,
        CredentialProvider credentialProvider
) {
}
