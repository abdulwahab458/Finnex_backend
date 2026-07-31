package com.finnex.finance_app.domain.loans.controller;

import com.finnex.finance_app.common.response.ApiResponse;
import com.finnex.finance_app.domain.loans.dto.request.CreateLoanRequest;
import com.finnex.finance_app.domain.loans.dto.request.RecordPaymentRequest;
import com.finnex.finance_app.domain.loans.dto.request.UpdateLoanRequest;
import com.finnex.finance_app.domain.loans.dto.response.LoanResponse;
import com.finnex.finance_app.domain.loans.service.LoanService;
import com.finnex.finance_app.domain.user.entity.User;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/loans")
@RequiredArgsConstructor
public class LoanController {

    private final LoanService loanService;

    @GetMapping
    public ApiResponse<List<LoanResponse>> getAllLoans(
            @AuthenticationPrincipal User user
    ) {

        return ApiResponse.ok(
                loanService.getAllLoans(user),
                "Loans retrieved successfully."
        );
    }

    @GetMapping("/{loanId}")
    public ApiResponse<LoanResponse> getLoanById(
            @AuthenticationPrincipal User user,
            @PathVariable UUID loanId
    ) {

        return ApiResponse.ok(
                loanService.getLoanById(user, loanId),
                "Loan retrieved successfully."
        );
    }

    @PostMapping
    public ApiResponse<LoanResponse> createLoan(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody CreateLoanRequest request
    ) {

        return ApiResponse.ok(
                loanService.createLoan(user, request),
                "Loan created successfully."
        );
    }

    @PutMapping("/{loanId}")
    public ApiResponse<LoanResponse> updateLoan(
            @AuthenticationPrincipal User user,
            @PathVariable UUID loanId,
            @Valid @RequestBody UpdateLoanRequest request
    ) {

        return ApiResponse.ok(
                loanService.updateLoan(user, loanId, request),
                "Loan updated successfully."
        );
    }

    @DeleteMapping("/{loanId}")
    public ApiResponse<Void> deleteLoan(
            @AuthenticationPrincipal User user,
            @PathVariable UUID loanId
    ) {

        loanService.deleteLoan(user, loanId);

        return ApiResponse.ok(
                null,
                "Loan deleted successfully."
        );
    }

    @PostMapping("/{loanId}/payments")
    public ApiResponse<LoanResponse> recordPayment(
            @AuthenticationPrincipal User user,
            @PathVariable UUID loanId,
            @Valid @RequestBody RecordPaymentRequest request
    ) {

        return ApiResponse.ok(
                loanService.recordPayment(user, loanId, request),
                "Payment recorded successfully."
        );
    }
}
