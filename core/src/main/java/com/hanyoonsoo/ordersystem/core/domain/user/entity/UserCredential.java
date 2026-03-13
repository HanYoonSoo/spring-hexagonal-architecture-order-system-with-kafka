package com.hanyoonsoo.ordersystem.core.domain.user.entity;

import com.hanyoonsoo.ordersystem.core.domain.common.SoftDeleteTimeEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(
        name = "user_credential",
        uniqueConstraints = @UniqueConstraint(
                name = "uq_user_credential_login_id_deleted_at",
                columnNames = {"login_id", "deleted_at"}
        )
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserCredential extends SoftDeleteTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, updatable = false)
    private Long id;

    @Column(name = "login_id", nullable = false)
    private String loginId;

    @Column(name = "value", nullable = false)
    private String value;

    @Enumerated(EnumType.STRING)
    @Column(name = "provider", nullable = false)
    private CredentialProvider provider;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    private UserCredential(String loginId, String value, CredentialProvider provider, User user) {
        this.loginId = loginId;
        this.value = value;
        this.provider = provider;
        this.user = user;
    }

    public static UserCredential of(String loginId, String value, CredentialProvider provider, User user) {
        UserCredential credential = new UserCredential(loginId, value, provider, user);
        user.addUserCredential(credential);
        return credential;
    }

    public void modifyValue(String value) {
        this.value = value;
    }
}
