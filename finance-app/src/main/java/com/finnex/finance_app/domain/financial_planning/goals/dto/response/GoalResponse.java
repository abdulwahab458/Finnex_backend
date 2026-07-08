package com.finnex.finance_app.domain.financial_planning.goals.dto.response;

import com.finnex.finance_app.common.enums.GoalCategory;
import com.finnex.finance_app.common.enums.GoalStatus;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Data
public class GoalResponse {

    private UUID id;

    private String name;

    private GoalCategory category;

    private BigDecimal targetAmount;

    private BigDecimal currentAmount;

    private BigDecimal remainingAmount;

    private BigDecimal progressPercentage;

    private GoalStatus status;

    private LocalDate targetDate;
}