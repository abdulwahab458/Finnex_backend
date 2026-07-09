package com.finnex.finance_app.domain.dashboard.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class GoalSummaryResponse {

    private Integer totalGoals;

    private Integer completedGoals;

    private Integer inProgressGoals;

    private BigDecimal totalSavedAmount;

    private BigDecimal totalTargetAmount;

}
