package com.hanyoonsoo.ordersystem.api.auth.config;

import java.util.Arrays;

public enum AllowedPaths {
    AUTH("/api/v1/auth/**"),
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
