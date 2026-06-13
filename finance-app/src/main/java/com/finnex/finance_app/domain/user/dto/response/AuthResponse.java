package com.finnex.finance_app.domain.user.dto.response;

import lombok.Data;

@Data
public class AuthResponse {

    private String accessToken;

    private String refreshToken;

    private String tokenType;

    private long expiresIn;

    private UserResponse user;
}