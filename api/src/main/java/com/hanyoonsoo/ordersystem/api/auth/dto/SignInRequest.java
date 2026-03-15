package com.hanyoonsoo.ordersystem.api.auth.dto;

import com.hanyoonsoo.ordersystem.application.auth.dto.SignInCommand;
import com.hanyoonsoo.ordersystem.core.domain.user.entity.CredentialProvider;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "로그인 요청")
public record SignInRequest(
        @Schema(description = "사용자 인증 정보(email:password)", example = "test@example.com:test1234")
        @NotBlank(message = "인증 정보는 비어있을 수 없습니다")
        String credential,

        @Schema(description = "계정 Provider", defaultValue = "LOCAL", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
        CredentialProvider credentialProvider
) {
    public SignInRequest {
        credentialProvider = credentialProvider == null ? CredentialProvider.LOCAL : credentialProvider;
    }

    public SignInCommand toCommand() {
        return new SignInCommand(credential, credentialProvider);
    }
}
