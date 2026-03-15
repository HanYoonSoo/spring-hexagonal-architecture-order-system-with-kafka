package com.hanyoonsoo.ordersystem.api.user.dto.request;

import com.hanyoonsoo.ordersystem.application.user.dto.SignUpCommand;
import com.hanyoonsoo.ordersystem.core.domain.user.entity.CredentialProvider;
import com.hanyoonsoo.ordersystem.core.domain.user.entity.Role;
import jakarta.validation.constraints.NotBlank;

public record SignUpRequest(
        @NotBlank String name,
        @NotBlank String credential,
        CredentialProvider credentialProvider,
        Role userRole
) {
        public SignUpRequest {
                credentialProvider = credentialProvider == null ? CredentialProvider.LOCAL : credentialProvider;
                userRole = userRole == null ? Role.USER : userRole;
        }

        public SignUpCommand toCommand() {
                return new SignUpCommand(name, credential, credentialProvider, userRole);
        }
}
