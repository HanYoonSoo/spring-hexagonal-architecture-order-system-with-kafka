package com.hanyoonsoo.ordersystem.core.domain.user.entity;

import com.hanyoonsoo.ordersystem.core.domain.common.SoftDeleteTimeEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Entity
@Table(name = "users")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends SoftDeleteTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "recovery_email")
    private String recoveryEmail;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private final List<UserRole> userRoles = new ArrayList<>();

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private final List<UserCredential> userCredentials = new ArrayList<>();

    private User(String name, String recoveryEmail) {
        this.name = name;
        this.recoveryEmail = recoveryEmail;
    }

    public static User of(String name, String recoveryEmail) {
        return new User(name, recoveryEmail);
    }

    public void modifyRecoveryEmail(String recoveryEmail) {
        this.recoveryEmail = recoveryEmail;
    }

    public void addUserRole(UserRole userRole) {
        this.userRoles.add(userRole);
    }

    public void addUserCredential(UserCredential userCredential) {
        this.userCredentials.add(userCredential);
    }
}
