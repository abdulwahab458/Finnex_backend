package com.finnex.finance_app.domain.intelligence.stimulation.service;
import com.finnex.finance_app.domain.intelligence.stimulation.dto.GoalSimulationResponse;
import com.finnex.finance_app.domain.user.entity.User;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public interface SimulationService {

    GoalSimulationResponse simulateGoalWithMonthlyContribution(
            User user,
            UUID goalId,
            BigDecimal monthlyContribution
    );

    GoalSimulationResponse simulateGoalWithLumpSumAndMonthlyContribution(
            User user,
            UUID goalId,
            BigDecimal lumpSumContribution,
            BigDecimal monthlyContribution
    );

    GoalSimulationResponse calculateRequiredMonthlyContribution(
            User user,
            UUID goalId,
            LocalDate desiredCompletionDate
    );

    GoalSimulationResponse checkGoalAchievability(
            User user,
            UUID goalId,
            BigDecimal monthlyContribution,
            int months
    );
}
