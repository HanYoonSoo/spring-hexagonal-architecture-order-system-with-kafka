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
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id = UUID.randomUUID();

    @Column(name = "name", nullable = false)
    private String name;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "user")
    private final List<UserRole> userRoles = new ArrayList<>();

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "user")
    private final List<UserCredential> userCredentials = new ArrayList<>();

    private User(String name) {
        this.name = name;
    }

    public static User from(String name) {
        return new User(name);
    }

    public void addUserRole(UserRole userRole) {
        this.userRoles.add(userRole);
    }

    public void addUserCredential(UserCredential userCredential) {
        this.userCredentials.add(userCredential);
    }
}
