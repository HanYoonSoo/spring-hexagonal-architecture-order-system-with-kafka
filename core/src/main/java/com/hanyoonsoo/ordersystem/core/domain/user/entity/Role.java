package com.hanyoonsoo.ordersystem.core.domain.user.entity;

public enum Role {
    AGENTF_ONLINE,
    AGENTF_OFFLINE,
    AGENTF_TRANSACTION,
    AGENTF_ADMIN,
    AGENTF_FOOD,
    AGENTF_CHALLENGER;

    public String toSpringRole() {
        return "ROLE_" + name();
    }

    public static Role[] allRoles() {
        return values();
    }
}
