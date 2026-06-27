package com.finnex.finance_app.domain.user.controller;

import com.finnex.finance_app.common.response.ApiResponse;
import com.finnex.finance_app.domain.user.dto.request.UpdateProfile;
import com.finnex.finance_app.domain.user.dto.response.UserResponse;
import com.finnex.finance_app.domain.user.entity.User;
import com.finnex.finance_app.domain.user.mapper.UserMapper;
import com.finnex.finance_app.domain.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/users")
@RequiredArgsConstructor
public class UserController {
    private final UserMapper usermapper;
    private final UserService userService;

    @GetMapping("/me")
    public ApiResponse<UserResponse> currentUser(@AuthenticationPrincipal User user) {
        return ApiResponse.ok(usermapper.toresponse(user),"Current user fetched !");
    }

    @PutMapping("/me")
    public  ApiResponse<UserResponse> updateUser(@AuthenticationPrincipal User user, @RequestBody @Valid UpdateProfile request) {
        UserResponse userResponse = userService.updateUser(user,request);
        return ApiResponse.ok(userResponse,"User updated Sucessfully !");

    }



}
