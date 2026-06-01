package com.finnex.finance_app.domain.user.entity;

import com.finnex.finance_app.common.audit.Auditable;
import com.finnex.finance_app.common.enums.RoleType;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "users")
public class User extends Auditable {
    @Id
    @GeneratedValue
    @UuidGenerator
    private UUID id;
    @Column(nullable = false, unique = true)
    private String email;
    @Column(nullable = false)
    private String FirstName;
    @Column(nullable = false)
    private String LastName;
    @Column(nullable = false)
    private String Password;
    @Enumerated(EnumType.STRING)
    private RoleType role;
    @Column(nullable = false)
    private Boolean emailVerified;
    private boolean mfaEnabled;
    private String mfaSecret;
    private String avatarUrl;
    private LocalDateTime lastLoginAt;
    private boolean accountNonLocked;
    private int failedLoginAttempts;
    private LocalDateTime lockoutTime;

}
