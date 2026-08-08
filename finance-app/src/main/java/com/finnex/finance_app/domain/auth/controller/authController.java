package com.finnex.finance_app.domain.auth.controller;

import com.finnex.finance_app.common.ratelimit.Service.RateLimitService;
import com.finnex.finance_app.common.response.ApiResponse;
import com.finnex.finance_app.domain.auth.service.AuthService;
import com.finnex.finance_app.domain.user.dto.request.LoginDTO;
import com.finnex.finance_app.domain.user.dto.request.RefreshTokenRequest;
import com.finnex.finance_app.domain.user.dto.request.RegisterRequestDTO;
import com.finnex.finance_app.domain.user.dto.response.AuthResponse;
import com.finnex.finance_app.domain.user.dto.response.UserResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;

@RestController
@RequestMapping("/api/v1/auth")
public class authController {
    @Autowired
    private AuthService authService;
    @Autowired
    private RateLimitService rateLimitService;

    @GetMapping("/ok")
    public String ok(){
        return "Ok working";
    }

    @PostMapping("/register")
    public ApiResponse<UserResponse> register(@Valid @RequestBody RegisterRequestDTO request,HttpServletRequest httprequest){
        rateLimitService.validateRequest(
                httprequest,
                "register",
                3,
                Duration.ofHours(1)
        );
        UserResponse userResponse = authService.register(request);
        return  ApiResponse.ok(userResponse,"User Registered Successfully");
    }

    @PostMapping("/login")
    public ApiResponse<AuthResponse> login(@Valid @RequestBody LoginDTO request, HttpServletRequest httprequest){
        rateLimitService.validateRequest(
                httprequest,
                "login",
                3,
                Duration.ofMinutes(1)

        );
        AuthResponse authResponse = authService.login(request);
        return  ApiResponse.ok(authResponse,"User Login Successfully");
    }

    @PostMapping("/refresh")
    public ApiResponse<AuthResponse> refreshToken( @Valid @RequestBody RefreshTokenRequest request,HttpServletRequest httprequest
    ) {
        rateLimitService.validateRequest(
                httprequest,
                "refresh",
                20,
                Duration.ofMinutes(1)
        );

        return ApiResponse.ok(
                authService.refreshToken(request),
                "Token refreshed successfully"
        );
    }
}
