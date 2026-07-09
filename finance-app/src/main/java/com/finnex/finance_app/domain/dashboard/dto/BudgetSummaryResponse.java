package com.finnex.finance_app.domain.dashboard.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class BudgetSummaryResponse {
    private Integer totalBudgets;

    private Integer onTrack;

    private Integer warning;

    private Integer overBudget;

    private BigDecimal totalBudgetAmount;

    private BigDecimal totalSpent;
}
