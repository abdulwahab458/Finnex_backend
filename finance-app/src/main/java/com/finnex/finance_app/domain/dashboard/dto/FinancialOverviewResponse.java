package com.finnex.finance_app.domain.dashboard.dto;

import lombok.Data;

import java.math.BigDecimal;
@Data
public class FinancialOverviewResponse {
    private BigDecimal totalBalance;

    private BigDecimal monthlyIncome;

    private BigDecimal monthlyExpense;

    private BigDecimal netSavings;

    private BigDecimal savingsRate;
}
