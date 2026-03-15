package com.hanyoonsoo.ordersystem.core.domain.user.entity;

public enum Role {
    USER, ADMIN;

    public String toSpringRole() {
        return "ROLE_" + name();
    }

    public static Role[] allRoles() {
        return values();
    }
}
