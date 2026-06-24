package com.finnex.finance_app.domain.account.Controller;

import com.finnex.finance_app.common.enums.AccountType;
import com.finnex.finance_app.common.response.ApiResponse;
import com.finnex.finance_app.domain.account.Service.AccountService;
import com.finnex.finance_app.domain.account.dto.request.CreateAccountRequest;
import com.finnex.finance_app.domain.account.dto.response.AccountResponse;
import com.finnex.finance_app.domain.account.entity.Account;
import com.finnex.finance_app.domain.user.entity.User;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("api/v1/accounts")
@RequiredArgsConstructor
public class AccountController {
    private final AccountService accountService;
    @PostMapping("/create-account")
    public ApiResponse<AccountResponse> createAccount(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody CreateAccountRequest request
            ) {
        return ApiResponse.ok(
                accountService.createAccount(user,request),
                "Account Created Successfully");

    }


    @GetMapping
    public ApiResponse<List<AccountResponse>> getAccounts(
            @AuthenticationPrincipal User user,
            @RequestParam(required = false)
            AccountType accountType
    ){
        return ApiResponse.ok(
                accountService.getAccounts(user,accountType),
                "Accounts Fetched Successfully"
        );
    }


    @GetMapping("/{id}")
    public  ApiResponse<AccountResponse> getAccountById(
            @AuthenticationPrincipal User user,
            @PathVariable UUID id
    ){
        return ApiResponse.ok(accountService.getAccountById(user,id),"Account Fetched Successfully");
    }
}
