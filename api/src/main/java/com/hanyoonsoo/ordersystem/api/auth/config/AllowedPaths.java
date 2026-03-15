package com.hanyoonsoo.ordersystem.api.auth.config;

import java.util.Arrays;

public enum AllowedPaths {
    SIGN_IN("/api/v1/auth/sign-in"),
    TOKEN_REISSUE("/api/v1/auth/reissue"),
    USER_SIGN_UP("/api/v1/users"),
    HEALTH("/actuator/health"),
    ERROR("/error"),
    FAVICON("/favicon.ico"),
    SWAGGER_UI("/swagger-ui/**"),
    API_DOCS("/v3/api-docs/**");

    private final String path;

    AllowedPaths(String path) {
        this.path = path;
    }

    public String path() {
        return path;
    }

    public static String[] allowedPaths() {
        return Arrays.stream(values())
                .map(AllowedPaths::path)
                .distinct()
                .toArray(String[]::new);
    }
}
