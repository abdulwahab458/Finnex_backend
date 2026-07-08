package com.finnex.finance_app.domain.financial_planning.goals.controller;

import com.finnex.finance_app.common.response.ApiResponse;
import com.finnex.finance_app.domain.financial_planning.goals.dto.request.CreateGoalRequest;
import com.finnex.finance_app.domain.financial_planning.goals.dto.request.GoalContributionRequest;
import com.finnex.finance_app.domain.financial_planning.goals.dto.request.UpdateGoalRequest;
import com.finnex.finance_app.domain.financial_planning.goals.dto.response.GoalResponse;
import com.finnex.finance_app.domain.financial_planning.goals.service.GoalsService;
import com.finnex.finance_app.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/goals")
@RequiredArgsConstructor
public class GoalController {
    private final GoalsService goalsService;
    @PostMapping
    public ApiResponse<GoalResponse> createGoal(
            @AuthenticationPrincipal User user,
            @RequestBody CreateGoalRequest request
            ){
        return ApiResponse.ok(
                goalsService.createGoal(
                        user,
                        request
                ),
                "Goal Created Successfully"
        );
    }


    @GetMapping
    public ApiResponse<List<GoalResponse>>  getAllGoals(
            @AuthenticationPrincipal User user
    ){
        return ApiResponse.ok(
                goalsService.findAllGoals(user),
                "All Goals Fetched Successfully"
        );
    }
    @GetMapping("/{goalId}")
    public ApiResponse<GoalResponse>  getAllGoals(
            @AuthenticationPrincipal User user,
            @PathVariable UUID goalId
    ){
        return ApiResponse.ok(
                goalsService.findGoalById(user,goalId),
                "Goal Fetched Successfully"
        );
    }




    @PutMapping("/{goalId}")
    public ApiResponse<GoalResponse> updateGoalContribution(
            @AuthenticationPrincipal User user,
            @PathVariable UUID goalId,
            @RequestBody UpdateGoalRequest request
            ){
        return ApiResponse.ok(
                goalsService.updateGoal(user,goalId,request),
                "Goal Contribution added Successfully"
        );
    }
    @PostMapping("/{goalId}/contribute")
    public ApiResponse<GoalResponse> contributeToGoal(
            @AuthenticationPrincipal User user,
            @PathVariable UUID goalId,
            @RequestBody GoalContributionRequest request
            ){
        return ApiResponse.ok(
                goalsService.contributeToGoal(user,goalId,request),
                "Goal Contribution added Successfully"
        );
    }
    @DeleteMapping("/{goalId}")
    public ApiResponse<GoalResponse> deleteGoal(
            @AuthenticationPrincipal User user,
            @PathVariable UUID goalId
            ){
        goalsService.deleteGoal(user,goalId);
        return ApiResponse.ok(
                null,
                "Goal Contribution added Successfully"
        );
    }


}
