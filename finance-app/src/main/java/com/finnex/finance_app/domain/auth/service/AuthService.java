package com.finnex.finance_app.domain.auth.service;

import com.finnex.finance_app.domain.user.dto.request.LoginDTO;
import com.finnex.finance_app.domain.user.dto.request.RefreshTokenRequest;
import com.finnex.finance_app.domain.user.dto.request.RegisterRequestDTO;
import com.finnex.finance_app.domain.user.dto.response.AuthResponse;
import com.finnex.finance_app.domain.user.dto.response.UserResponse;

public interface AuthService  {
    UserResponse register(RegisterRequestDTO registerRequest);
    AuthResponse login(LoginDTO request);
    AuthResponse refreshToken(RefreshTokenRequest request);
}
