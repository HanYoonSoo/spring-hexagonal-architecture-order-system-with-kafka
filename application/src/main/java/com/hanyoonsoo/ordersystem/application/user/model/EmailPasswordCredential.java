package com.hanyoonsoo.ordersystem.application.user.model;

import com.hanyoonsoo.ordersystem.common.exception.ErrorCode;
import com.hanyoonsoo.ordersystem.common.exception.base.BadRequestException;

import java.util.regex.Pattern;

public record EmailPasswordCredential(
        String loginId,
        String plainPassword
) {

    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");

    public static EmailPasswordCredential from(String credential) {
        String[] parts = credential == null ? new String[0] : credential.split(":");
        if (parts.length != 2) {
            throw new BadRequestException(
                    ErrorCode.INVALID_CREDENTIAL_FORMAT,
                    "Credential should be 'email:password'."
            );
        }

        String email = parts[0].trim();
        String plainPassword = parts[1];
        if (!EMAIL_PATTERN.matcher(email).matches()) {
            throw new BadRequestException(ErrorCode.EMAIL_FORMAT_ERROR);
        }
        if (plainPassword.length() < 8 || plainPassword.length() > 30) {
            throw new BadRequestException(ErrorCode.PASSWORD_LENGTH_ERROR);
        }

        return new EmailPasswordCredential(email, plainPassword);
    }
}
