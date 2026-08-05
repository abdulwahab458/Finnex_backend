package com.finnex.finance_app.domain.intelligence.stimulation.service.impl;

import com.finnex.finance_app.common.exceptions.BadRequestException;
import com.finnex.finance_app.common.exceptions.ResourceNotFound;
import com.finnex.finance_app.domain.financial_planning.goals.entity.Goals;
import com.finnex.finance_app.domain.financial_planning.goals.repository.GoalRepository;

import com.finnex.finance_app.domain.intelligence.stimulation.dto.GoalSimulationResponse;
import com.finnex.finance_app.domain.intelligence.stimulation.service.SimulationService;
import com.finnex.finance_app.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SimulationServiceImpl implements SimulationService {

    private final GoalRepository goalRepository;


    // ============================================================
    // MONTHLY CONTRIBUTION
    // ============================================================

    @Override
    public GoalSimulationResponse simulateGoalWithMonthlyContribution(
            User user,
            UUID goalId,
            BigDecimal monthlyContribution
    ) {

        validatePositiveAmount(
                monthlyContribution,
                "Monthly contribution"
        );

        Goals goal = getGoal(user, goalId);

        BigDecimal remainingAmount =
                calculateRemainingAmount(goal);

        if (remainingAmount.compareTo(BigDecimal.ZERO) == 0) {

            return buildCompletedGoalResponse(goal);
        }

        int monthsRequired =
                remainingAmount
                        .divide(
                                monthlyContribution,
                                0,
                                RoundingMode.CEILING
                        )
                        .intValueExact();

        LocalDate estimatedCompletionDate =
                LocalDate.now()
                        .plusMonths(monthsRequired);

        boolean achievableByTargetDate =
                goal.getTargetDate() == null
                        || !estimatedCompletionDate
                        .isAfter(goal.getTargetDate());

        return GoalSimulationResponse.builder()
                .goalName(goal.getName())
                .targetAmount(goal.getTargetAmount())
                .currentAmount(goal.getCurrentAmount())
                .remainingAmount(remainingAmount)
                .monthlyContribution(monthlyContribution)
                .lumpSumContribution(BigDecimal.ZERO)
                .monthsRequired(monthsRequired)
                .estimatedCompletionDate(estimatedCompletionDate)
                .achievable(achievableByTargetDate)
                .targetDate(goal.getTargetDate())
                .build();
    }


    // ============================================================
    // LUMP SUM + MONTHLY CONTRIBUTION
    // ============================================================

    @Override
    public GoalSimulationResponse simulateGoalWithLumpSumAndMonthlyContribution(
            User user,
            UUID goalId,
            BigDecimal lumpSumContribution,
            BigDecimal monthlyContribution
    ) {

        validateNonNegativeAmount(
                lumpSumContribution,
                "Lump sum contribution"
        );

        validatePositiveAmount(
                monthlyContribution,
                "Monthly contribution"
        );

        Goals goal = getGoal(user, goalId);

        BigDecimal remainingBeforeLumpSum =
                calculateRemainingAmount(goal);

        if (remainingBeforeLumpSum.compareTo(BigDecimal.ZERO) == 0) {

            return buildCompletedGoalResponse(goal);
        }

        BigDecimal remainingAfterLumpSum =
                remainingBeforeLumpSum
                        .subtract(lumpSumContribution)
                        .max(BigDecimal.ZERO);

        if (remainingAfterLumpSum.compareTo(BigDecimal.ZERO) == 0) {

            return GoalSimulationResponse.builder()
                    .goalName(goal.getName())
                    .targetAmount(goal.getTargetAmount())
                    .currentAmount(goal.getCurrentAmount())
                    .remainingAmount(BigDecimal.ZERO)
                    .monthlyContribution(monthlyContribution)
                    .lumpSumContribution(lumpSumContribution)
                    .monthsRequired(0)
                    .estimatedCompletionDate(LocalDate.now())
                    .achievable(true)
                    .targetDate(goal.getTargetDate())
                    .build();
        }

        int monthsRequired =
                remainingAfterLumpSum
                        .divide(
                                monthlyContribution,
                                0,
                                RoundingMode.CEILING
                        )
                        .intValueExact();

        LocalDate estimatedCompletionDate =
                LocalDate.now()
                        .plusMonths(monthsRequired);

        boolean achievableByTargetDate =
                goal.getTargetDate() == null
                        || !estimatedCompletionDate
                        .isAfter(goal.getTargetDate());

        return GoalSimulationResponse.builder()
                .goalName(goal.getName())
                .targetAmount(goal.getTargetAmount())
                .currentAmount(goal.getCurrentAmount())
                .remainingAmount(remainingAfterLumpSum)
                .monthlyContribution(monthlyContribution)
                .lumpSumContribution(lumpSumContribution)
                .monthsRequired(monthsRequired)
                .estimatedCompletionDate(estimatedCompletionDate)
                .achievable(achievableByTargetDate)
                .targetDate(goal.getTargetDate())
                .build();
    }


    // ============================================================
    // REQUIRED MONTHLY CONTRIBUTION
    // ============================================================

    @Override
    public GoalSimulationResponse calculateRequiredMonthlyContribution(
            User user,
            UUID goalId,
            LocalDate desiredCompletionDate
    ) {

        if (desiredCompletionDate == null) {
            throw new BadRequestException(
                    "Desired completion date is required."
            );
        }

        if (!desiredCompletionDate.isAfter(LocalDate.now())) {
            throw new BadRequestException(
                    "Desired completion date must be in the future."
            );
        }

        Goals goal = getGoal(user, goalId);

        BigDecimal remainingAmount =
                calculateRemainingAmount(goal);

        if (remainingAmount.compareTo(BigDecimal.ZERO) == 0) {

            return buildCompletedGoalResponse(goal);
        }

        long monthsAvailable =
                ChronoUnit.MONTHS.between(
                        YearMonth.from(LocalDate.now()),
                        YearMonth.from(desiredCompletionDate)
                );

        /*
         * Example:
         * Today = August
         * Desired date = September
         *
         * There is one monthly contribution opportunity.
         */
        if (monthsAvailable <= 0) {
            monthsAvailable = 1;
        }

        BigDecimal requiredMonthlyContribution =
                remainingAmount.divide(
                        BigDecimal.valueOf(monthsAvailable),
                        2,
                        RoundingMode.CEILING
                );

        return GoalSimulationResponse.builder()
                .goalName(goal.getName())
                .targetAmount(goal.getTargetAmount())
                .currentAmount(goal.getCurrentAmount())
                .remainingAmount(remainingAmount)
                .requiredMonthlyContribution(
                        requiredMonthlyContribution
                )
                .monthsRequired(
                        Math.toIntExact(monthsAvailable)
                )
                .estimatedCompletionDate(
                        desiredCompletionDate
                )
                .achievable(true)
                .targetDate(goal.getTargetDate())
                .build();
    }


    // ============================================================
    // ACHIEVABILITY
    // ============================================================

    @Override
    public GoalSimulationResponse checkGoalAchievability(
            User user,
            UUID goalId,
            BigDecimal monthlyContribution,
            int months
    ) {

        validatePositiveAmount(
                monthlyContribution,
                "Monthly contribution"
        );

        if (months <= 0) {
            throw new BadRequestException(
                    "Number of months must be greater than zero."
            );
        }

        Goals goal = getGoal(user, goalId);

        BigDecimal remainingAmount =
                calculateRemainingAmount(goal);

        if (remainingAmount.compareTo(BigDecimal.ZERO) == 0) {

            return buildCompletedGoalResponse(goal);
        }

        BigDecimal projectedContribution =
                monthlyContribution.multiply(
                        BigDecimal.valueOf(months)
                );

        boolean achievable =
                projectedContribution.compareTo(
                        remainingAmount
                ) >= 0;

        int actualMonthsRequired =
                remainingAmount
                        .divide(
                                monthlyContribution,
                                0,
                                RoundingMode.CEILING
                        )
                        .intValueExact();

        LocalDate estimatedCompletionDate =
                LocalDate.now()
                        .plusMonths(actualMonthsRequired);

        return GoalSimulationResponse.builder()
                .goalName(goal.getName())
                .targetAmount(goal.getTargetAmount())
                .currentAmount(goal.getCurrentAmount())
                .remainingAmount(remainingAmount)
                .monthlyContribution(monthlyContribution)
                .lumpSumContribution(BigDecimal.ZERO)
                .monthsRequired(actualMonthsRequired)
                .estimatedCompletionDate(
                        estimatedCompletionDate
                )
                .achievable(achievable)
                .targetDate(goal.getTargetDate())
                .build();
    }


    // ============================================================
    // HELPERS
    // ============================================================

    private Goals getGoal(
            User user,
            UUID goalId
    ) {

        return goalRepository
                .findByIdAndUser(goalId, user)
                .orElseThrow(
                        () -> new ResourceNotFound(
                                "Goal not found."
                        )
                );
    }


    private BigDecimal calculateRemainingAmount(
            Goals goal
    ) {

        return goal.getTargetAmount()
                .subtract(goal.getCurrentAmount())
                .max(BigDecimal.ZERO);
    }


    private void validatePositiveAmount(
            BigDecimal amount,
            String fieldName
    ) {

        if (amount == null
                || amount.compareTo(BigDecimal.ZERO) <= 0) {

            throw new BadRequestException(
                    fieldName + " must be greater than zero."
            );
        }
    }


    private void validateNonNegativeAmount(
            BigDecimal amount,
            String fieldName
    ) {

        if (amount == null
                || amount.compareTo(BigDecimal.ZERO) < 0) {

            throw new BadRequestException(
                    fieldName + " cannot be negative."
            );
        }
    }


    private GoalSimulationResponse buildCompletedGoalResponse(
            Goals goal
    ) {

        return GoalSimulationResponse.builder()
                .goalName(goal.getName())
                .targetAmount(goal.getTargetAmount())
                .currentAmount(goal.getCurrentAmount())
                .remainingAmount(BigDecimal.ZERO)
                .monthsRequired(0)
                .estimatedCompletionDate(LocalDate.now())
                .achievable(true)
                .targetDate(goal.getTargetDate())
                .build();
    }
}
