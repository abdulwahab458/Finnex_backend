package com.finnex.finance_app.domain.auth.controller;

import com.finnex.finance_app.common.response.ApiResponse;
import com.finnex.finance_app.domain.auth.service.AuthService;
import com.finnex.finance_app.domain.user.dto.request.LoginDTO;
import com.finnex.finance_app.domain.user.dto.request.RegisterRequestDTO;
import com.finnex.finance_app.domain.user.dto.response.AuthResponse;
import com.finnex.finance_app.domain.user.dto.response.UserResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
public class authController {
    @Autowired
    private AuthService authService;

    @GetMapping("/ok")
    public String ok(){
        return "Ok working";
    }

    @PostMapping("/register")
    public ApiResponse<UserResponse> register(@RequestBody RegisterRequestDTO request){
        UserResponse userResponse = authService.register(request);
        return  ApiResponse.ok(userResponse,"User Registered Successfully");
    }

    @PostMapping("/login")
    public ApiResponse<AuthResponse> login(@RequestBody LoginDTO request){
        AuthResponse authResponse = authService.login(request);
        return  ApiResponse.ok(authResponse,"User Login Successfully");
    }
}
