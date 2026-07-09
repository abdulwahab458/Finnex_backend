package com.finnex.finance_app.domain.dashboard.dto;

import lombok.Data;

@Data
public class DashboardResponse {
    private FinancialOverviewResponse financialOverview;

    private PortfolioSummaryResponse portfolioSummary;

    private BudgetSummaryResponse budgetSummary;

    private GoalSummaryResponse goalSummary;

    private FinancialHealthResponse financialHealth;
}
