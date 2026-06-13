package com.finnex.finance_app.domain.user.entity;

import com.finnex.finance_app.common.audit.Auditable;
import com.finnex.finance_app.common.enums.RoleType;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.UuidGenerator;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "users")
@Data
public class User extends Auditable implements UserDetails  {
    @Id
    @GeneratedValue
    @UuidGenerator
    private UUID id;
    @Column(nullable = false, unique = true)
    private String email;
    @Column(nullable = false)
    private String firstName;
    @Column(nullable = false)
    private String lastName;
    @Column(nullable = false)
    private String password;
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

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(
                new SimpleGrantedAuthority("ROLE_" + role.name())
        );
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonLocked() {
        return accountNonLocked;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
