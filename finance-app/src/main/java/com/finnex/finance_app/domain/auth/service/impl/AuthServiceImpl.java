package com.finnex.finance_app.domain.auth.service.impl;

import com.finnex.finance_app.common.enums.RoleType;
import com.finnex.finance_app.common.exceptions.DuplicateResourceException;
import com.finnex.finance_app.common.exceptions.ResourceNotFound;
import com.finnex.finance_app.config.JwtProperties;
import com.finnex.finance_app.domain.auth.service.AuthService;
import com.finnex.finance_app.domain.user.dto.request.LoginDTO;
import com.finnex.finance_app.domain.user.dto.request.RegisterRequestDTO;
import com.finnex.finance_app.domain.user.dto.response.AuthResponse;
import com.finnex.finance_app.domain.user.dto.response.UserResponse;
import com.finnex.finance_app.domain.user.entity.User;
import com.finnex.finance_app.domain.user.mapper.UserMapper;
import com.finnex.finance_app.domain.user.repository.UserRepository;
import com.finnex.finance_app.security.CustomUserDetailsService;
import com.finnex.finance_app.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl  implements AuthService {
    private final JwtProperties jwtProperties;
    private final UserRepository userRepository;
    private final  UserMapper userMapper;
    private final  PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public UserResponse register(RegisterRequestDTO registerRequest) {
        if(userRepository.existsByEmail(registerRequest.getEmail())){
            throw  new DuplicateResourceException("Email already exists");
        }

        User user = userMapper.toEntity(registerRequest);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole(RoleType.USER);
        user.setEmailVerified(false);
        user.setAccountNonLocked(true);
        userRepository.save(user);
        return userMapper.toresponse(user);

    }

    @Override
    public AuthResponse login(LoginDTO request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );
        User user = userRepository.findByEmail(
                        request.getEmail()
                )
                .orElseThrow(() ->
                        new ResourceNotFound(
                                "User not found"
                        ));

        String accessToken = jwtTokenProvider.generateaccessToken(user);
        String refreshToken = jwtTokenProvider.generateRefreshToken(user);
        AuthResponse authResponse = new AuthResponse();
        authResponse.setAccessToken(accessToken);
        authResponse.setRefreshToken(refreshToken);
        authResponse.setTokenType("Bearer");
        authResponse.setExpiresIn(
                jwtProperties.getAccessTokenExpiration()
        );
        authResponse.setUser(userMapper.toresponse(user));
        return authResponse;
    }
}
