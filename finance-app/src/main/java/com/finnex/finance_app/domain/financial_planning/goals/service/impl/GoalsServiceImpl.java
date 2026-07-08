package com.finnex.finance_app.domain.financial_planning.goals.service.impl;

import com.finnex.finance_app.common.enums.GoalStatus;
import com.finnex.finance_app.common.exceptions.BadRequestException;
import com.finnex.finance_app.common.exceptions.DuplicateResourceException;
import com.finnex.finance_app.common.exceptions.ResourceNotFound;
import com.finnex.finance_app.domain.financial_planning.goals.dto.request.CreateGoalRequest;
import com.finnex.finance_app.domain.financial_planning.goals.dto.request.GoalContributionRequest;
import com.finnex.finance_app.domain.financial_planning.goals.dto.request.UpdateGoalRequest;
import com.finnex.finance_app.domain.financial_planning.goals.dto.response.GoalResponse;
import com.finnex.finance_app.domain.financial_planning.goals.entity.Goals;
import com.finnex.finance_app.domain.financial_planning.goals.mapper.GoalMapper;
import com.finnex.finance_app.domain.financial_planning.goals.repository.GoalRepository;
import com.finnex.finance_app.domain.financial_planning.goals.service.GoalsService;
import com.finnex.finance_app.domain.user.entity.User;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GoalsServiceImpl implements GoalsService {
    private final GoalRepository goalRepository;
    private final GoalMapper goalMapper;

    @Override
    @Transactional
    public GoalResponse createGoal(User user, CreateGoalRequest request) {
        boolean exists = goalRepository.existsByUserAndName(user, request.getName());
        if (exists) {
            throw new DuplicateResourceException("Goal already exists");
        }

        Goals goal = goalMapper.toEntity(request);
        goal.setUser(user);
        goal.setCurrentAmount(BigDecimal.ZERO);
        goal.setStatus(GoalStatus.NOT_STARTED);
        goal = goalRepository.save(goal);
        return buildGoalResponse(goal);

    }

    @Override
    public List<GoalResponse> findAllGoals(User user) {
        List<Goals> goals = goalRepository.findByUserOrderByCreatedAtDesc(user);
        return goals.stream()
                .map(this::buildGoalResponse)
                .toList();
    }

    @Override
    public GoalResponse findGoalById(User user, UUID goalId) {
        Goals goal = goalRepository.findByIdAndUser(
                goalId,
                user
        ).orElseThrow(
                () -> new ResourceNotFound("Goal not found.")
        );
        updateGoalStatus(goal);
        return buildGoalResponse(goal);
    }

    @Override
    @Transactional
    public GoalResponse updateGoal(User user, UUID goalId, UpdateGoalRequest request) {
        Goals goal = goalRepository.findByIdAndUser(
                goalId,
                user
        ).orElseThrow(
                () -> new ResourceNotFound("Goal not found.")
        );
        boolean goalNameChanged =
                !goal.getName().equals(request.getName());

        if (goalNameChanged) {

            boolean exists =
                    goalRepository.existsByUserAndName(
                            user,
                            request.getName()
                    );

            if (exists) {
                throw new BadRequestException(
                        "Goal with this name already exists."
                );
            }
        }

        goal.setName(request.getName());
        goal.setCategory(request.getCategory());
        goal.setTargetAmount(request.getTargetAmount());
        goal.setTargetDate(request.getTargetDate());
        updateGoalStatus(goal);
        goal = goalRepository.save(goal);
        return buildGoalResponse(goal);
    }

    @Override
    @Transactional
    public GoalResponse contributeToGoal(User user, UUID goalId, GoalContributionRequest request) {
        Goals goal = goalRepository.findByIdAndUser(
                goalId,
                user
        ).orElseThrow(
                () -> new ResourceNotFound("Goal not found.")
        );

        goal.setCurrentAmount(
                goal.getCurrentAmount()
                        .add(request.getAmount())
        );
        updateGoalStatus(goal);
        goal = goalRepository.save(goal);
        return buildGoalResponse(goal);
    }

    @Override
    @Transactional
    public void deleteGoal(User user, UUID goalId) {
        Goals goal = goalRepository.findByIdAndUser(
                goalId,
                user
        ).orElseThrow(
                () -> new ResourceNotFound("Goal not found.")
        );
        goalRepository.delete(goal);
    }

    //helpers
    private void updateGoalStatus(Goals goal) {

        if (goal.getCurrentAmount().compareTo(BigDecimal.ZERO) == 0) {

            goal.setStatus(GoalStatus.NOT_STARTED);

        } else if (
                goal.getCurrentAmount()
                        .compareTo(goal.getTargetAmount()) >= 0
        ) {

            goal.setStatus(GoalStatus.COMPLETED);

        } else {

            goal.setStatus(GoalStatus.IN_PROGRESS);

        }
    }

    private GoalResponse buildGoalResponse(Goals goal) {

        GoalResponse response =
                goalMapper.toResponse(goal);

        BigDecimal remainingAmount =
                goal.getTargetAmount()
                        .subtract(goal.getCurrentAmount());

        response.setRemainingAmount(remainingAmount);

        BigDecimal progressPercentage;

        if (goal.getTargetAmount().compareTo(BigDecimal.ZERO) == 0) {

            progressPercentage = BigDecimal.ZERO;

        } else {

            progressPercentage =
                    goal.getCurrentAmount()
                            .divide(
                                    goal.getTargetAmount(),
                                    4,
                                    RoundingMode.HALF_UP
                            )
                            .multiply(BigDecimal.valueOf(100))
                            .setScale(2, RoundingMode.HALF_UP);
            if (progressPercentage.compareTo(BigDecimal.valueOf(100)) > 0) {
                progressPercentage = BigDecimal.valueOf(100);
            }
        }

        response.setProgressPercentage(progressPercentage);

        response.setStatus(goal.getStatus());

        return response;
    }

}
