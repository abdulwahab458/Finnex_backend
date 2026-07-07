package com.finnex.finance_app.domain.financial_planning.budgets.controller;

import com.finnex.finance_app.common.response.ApiResponse;
import com.finnex.finance_app.domain.financial_planning.budgets.dto.requet.CreateBudgetRequest;
import com.finnex.finance_app.domain.financial_planning.budgets.dto.requet.UpdateBudgetRequest;
import com.finnex.finance_app.domain.financial_planning.budgets.dto.response.BudgetResponse;
import com.finnex.finance_app.domain.financial_planning.budgets.service.BudgetService;
import com.finnex.finance_app.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/budgets")
@RequiredArgsConstructor
public class BudgetController {
    private final BudgetService budgetService;
    @GetMapping
    public ApiResponse<List<BudgetResponse>> budgets(@AuthenticationPrincipal User user){
        List<BudgetResponse> reponse = budgetService.getAllBudgets(user);
        return  ApiResponse.ok(
                reponse,
                "Fetched all Budgets Successfully"
        );

    }
    @PostMapping
    public ApiResponse<BudgetResponse> addBudget(
            @AuthenticationPrincipal User user,
            @RequestBody CreateBudgetRequest request
            ){
        return ApiResponse.ok(
                budgetService.createBudget(user,request),
                "Budget created"
        );
    }

    @PutMapping("/{id}")
    public ApiResponse<BudgetResponse> updateBudget(
            @AuthenticationPrincipal User user,
            @PathVariable UUID id,
            @RequestBody UpdateBudgetRequest request
    ){
        return ApiResponse.ok(
                budgetService.updateBudget(user,id,request),
                "Budget updated"
        );
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteBudget(
            @PathVariable UUID id,
            @AuthenticationPrincipal User currentUser
    ) {

        budgetService.deleteBudget(
                currentUser,
                id
        );

        return ApiResponse.ok(
                null,
                "Budget deleted"
        );
    }
}
