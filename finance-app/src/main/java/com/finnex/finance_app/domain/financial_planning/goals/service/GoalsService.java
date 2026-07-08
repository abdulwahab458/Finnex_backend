package com.finnex.finance_app.domain.financial_planning.goals.service;

import com.finnex.finance_app.domain.financial_planning.goals.dto.request.CreateGoalRequest;
import com.finnex.finance_app.domain.financial_planning.goals.dto.request.GoalContributionRequest;
import com.finnex.finance_app.domain.financial_planning.goals.dto.request.UpdateGoalRequest;
import com.finnex.finance_app.domain.financial_planning.goals.dto.response.GoalResponse;
import com.finnex.finance_app.domain.user.entity.User;

import java.util.List;
import java.util.UUID;

public interface GoalsService {
    GoalResponse createGoal(User user, CreateGoalRequest request);
    List<GoalResponse> findAllGoals(User user);
    GoalResponse findGoalById(User user, UUID goalId);
    GoalResponse updateGoal(User user, UUID goalId, UpdateGoalRequest request);
    GoalResponse contributeToGoal(User user, UUID goalId, GoalContributionRequest request);
    void deleteGoal(User user, UUID goalId);
}
