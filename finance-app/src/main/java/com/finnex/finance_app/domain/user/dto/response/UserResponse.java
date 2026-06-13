package com.finnex.finance_app.domain.user.dto.response;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class UserResponse {
    private UUID id;

    private String email;

    private String firstName;

    private String lastName;

    private String role;

    private String avatarUrl;

    private LocalDateTime lastLoginAt;

    private boolean mfaEnabled;
}
