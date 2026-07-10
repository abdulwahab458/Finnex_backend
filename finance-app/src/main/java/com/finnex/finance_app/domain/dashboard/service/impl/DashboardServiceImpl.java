package com.finnex.finance_app.domain.dashboard.service.impl;

import com.finnex.finance_app.common.enums.*;
import com.finnex.finance_app.domain.account.Repository.AccountRepository;
import com.finnex.finance_app.domain.account.entity.Account;
import com.finnex.finance_app.domain.dashboard.dto.*;
import com.finnex.finance_app.domain.dashboard.service.DashboardService;
import com.finnex.finance_app.domain.financial_planning.budgets.dto.response.BudgetResponse;
import com.finnex.finance_app.domain.financial_planning.budgets.entity.Budget;
import com.finnex.finance_app.domain.financial_planning.budgets.repository.BudgetRepository;
import com.finnex.finance_app.domain.financial_planning.budgets.service.BudgetService;
import com.finnex.finance_app.domain.financial_planning.goals.dto.response.GoalResponse;
import com.finnex.finance_app.domain.financial_planning.goals.repository.GoalRepository;
import com.finnex.finance_app.domain.financial_planning.goals.service.GoalsService;
import com.finnex.finance_app.domain.portfolio.dto.reponse.PortfolioResponse;
import com.finnex.finance_app.domain.portfolio.repository.PortfolioRepository;
import com.finnex.finance_app.domain.portfolio.service.PortfolioService;
import com.finnex.finance_app.domain.transactions.entity.Transaction;
import com.finnex.finance_app.domain.transactions.repository.TransactionRepository;
import com.finnex.finance_app.domain.user.entity.User;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.data.repository.core.support.RepositoryMethodInvocationListener;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {
    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;
    private final PortfolioService portfolioService;
    private final BudgetService budgetService;
    private final GoalsService goalsService;

    private final PortfolioRepository portfolioRepository;

    private final BudgetRepository budgetRepository;

    private final GoalRepository goalRepository;
    private final RepositoryMethodInvocationListener repositoryMethodInvocationListener;


    @Override
    public DashboardResponse getDashboard(User user) {
        DashboardResponse response = new DashboardResponse();
        response.setFinancialOverview(
                buildFinancialOverview(user)
        );
        response.setPortfolioSummary(buildPortfolioSummary(user));
        response.setBudgetSummary(
                buildBudgetSummary(user)
        );
        response.setGoalSummary(buildGoalSummary(user));
        response.setFinancialHealth(buildFinancialHealth(response));
        return response;
    }

    //helpers
    private FinancialOverviewResponse buildFinancialOverview(User user) {
        List<Account> accounts = accountRepository.findByUser(user);
        BigDecimal totalBalance =
                accounts.stream()
                        .map(Account::getBalance)
                        .reduce(
                                BigDecimal.ZERO,
                                BigDecimal::add
                        );
        FinancialOverviewResponse response = new FinancialOverviewResponse();
        response.setTotalBalance(totalBalance);
        LocalDate today = LocalDate.now();
        LocalDateTime startOfMonth =
                today.withDayOfMonth(1)
                        .atStartOfDay();
        LocalDateTime endOfMonth =
                today.withDayOfMonth(today.lengthOfMonth())
                        .atTime(LocalTime.MAX);

        List<Transaction> transactions = transactionRepository.findByAccountUserIdAndTypeAndStatusAndTransactionDateBetween(
                user.getId(),
                TransactionType.CREDIT,
                TransactionStatus.COMPLETED,
                startOfMonth,
                endOfMonth
        );
        BigDecimal monthlyIncome = transactions
                .stream()
                .filter(transaction ->
                        transaction.getStatus() == TransactionStatus.COMPLETED
                )
                .map(
                        Transaction::getAmount
                )
                .reduce(
                        BigDecimal.ZERO,
                        BigDecimal::add
                );

        List<Transaction> expenseTransactions = transactionRepository.findByAccountUserIdAndTypeAndStatusAndTransactionDateBetween(
                user.getId(),
                TransactionType.DEBIT,
                TransactionStatus.COMPLETED,
                startOfMonth,
                endOfMonth
        );

        BigDecimal monthlyExpense = expenseTransactions
                .stream()
                .filter(transaction ->
                        transaction.getStatus() == TransactionStatus.COMPLETED
                )
                .map(
                        Transaction::getAmount
                )
                .reduce(
                        BigDecimal.ZERO,
                        BigDecimal::add
                );

        BigDecimal netSavings =
                monthlyIncome.subtract(monthlyExpense);

        BigDecimal savingsRate;

        if (monthlyIncome.compareTo(BigDecimal.ZERO) == 0) {

            savingsRate = BigDecimal.ZERO;

        } else {

            savingsRate =
                    netSavings
                            .divide(
                                    monthlyIncome,
                                    4,
                                    RoundingMode.HALF_UP
                            )
                            .multiply(BigDecimal.valueOf(100))
                            .setScale(2, RoundingMode.HALF_UP);
        }
        response.setMonthlyIncome(monthlyIncome);
        response.setMonthlyExpense(monthlyExpense);
        response.setNetSavings(netSavings);
        response.setSavingsRate(savingsRate);
        return response;
    }
    private PortfolioSummaryResponse buildPortfolioSummary(User user) {
        PortfolioSummaryResponse response = new PortfolioSummaryResponse();
        List<PortfolioResponse> portfolios =
                portfolioService.getPortfolios(user);

        BigDecimal totalInvested = portfolios.stream()
                .map(PortfolioResponse::getTotalInvested)
                .reduce(
                        BigDecimal.ZERO,
                        BigDecimal::add
                );

        BigDecimal portfolioValue = portfolios.stream()
                .map(PortfolioResponse::getCurrentValue)
                .reduce(
                        BigDecimal.ZERO,
                        BigDecimal::add
                );

        BigDecimal totalProfit =
                portfolioValue.subtract(totalInvested);

        BigDecimal totalReturnPercentage;

        if (totalInvested.compareTo(BigDecimal.ZERO) == 0) {

            totalReturnPercentage = BigDecimal.ZERO;

        } else {

            totalReturnPercentage =
                    totalProfit
                            .divide(
                                    totalInvested,
                                    4,
                                    RoundingMode.HALF_UP
                            )
                            .multiply(BigDecimal.valueOf(100))
                            .setScale(2, RoundingMode.HALF_UP);
        }
        response.setTotalInvested(totalInvested);
        response.setCurrentValue(portfolioValue);
        response.setTotalReturnPercentage(totalReturnPercentage);

        return response;
    }

    private BudgetSummaryResponse buildBudgetSummary(User user) {

        BudgetSummaryResponse response =
                new BudgetSummaryResponse();

        List<BudgetResponse> budgets = budgetService.getAllBudgets(user);
        int onTrack = Math.toIntExact(budgets.stream()
                .filter(budget ->
                        budget.getStatus().equals(BudgetStatus.ON_TRACK)
                )
                .count());
        int warning = Math.toIntExact(budgets.stream()
                .filter(budget ->
                        budget.getStatus().equals(BudgetStatus.WARNING)
                )
                .count());
        int overBudget = Math.toIntExact(budgets.stream()
                .filter(budget ->
                        budget.getStatus().equals(BudgetStatus.WARNING)
                )
                .count());

        BigDecimal totalBudget =
                budgets.stream()
                                .map(BudgetResponse :: getTargetAmount)
                                        .reduce(
                                                BigDecimal.ZERO,
                                                BigDecimal::add
                                        );

        BigDecimal totalSpent = budgets.stream()
                .map(BudgetResponse::getCurrentSpent)
                .reduce(
                        BigDecimal.ZERO,
                        BigDecimal::add
                );

        response.setTotalBudgets(
                budgets.size()
        );
        response.setOnTrack(onTrack);
        response.setWarning(warning);
        response.setOverBudget(overBudget);
        response.setTotalBudgetAmount(totalBudget);
        response.setTotalSpent(totalSpent);
        return response;
    }

    private GoalSummaryResponse buildGoalSummary(User user){
        GoalSummaryResponse response = new GoalSummaryResponse();
        List<GoalResponse> goals = goalsService.findAllGoals(user);
        int completed = Math.toIntExact(goals.stream()
                .filter(goal -> goal.getStatus().equals(GoalStatus.COMPLETED))
                .count());
        int inProgress = Math.toIntExact(goals.stream()
                .filter(goal -> goal.getStatus().equals(GoalStatus.IN_PROGRESS))
                .count());

        BigDecimal totalSavedAmount = goals.stream()
                .map(GoalResponse::getCurrentAmount)
                .reduce(
                        BigDecimal.ZERO,
                        BigDecimal::add
                );
        BigDecimal totalTargetAmount = goals.stream()
                .map(GoalResponse::getTargetAmount)
                .reduce(
                        BigDecimal.ZERO,
                        BigDecimal::add
                );



        response.setTotalSavedAmount(totalSavedAmount);
        response.setTotalGoals(goals.size());
        response.setCompletedGoals(completed);
        response.setInProgressGoals(inProgress);
        response.setTotalSavedAmount(totalSavedAmount);
        response.setTotalTargetAmount(totalTargetAmount);

        return response;
    }

    private FinancialHealthResponse buildFinancialHealth(
            DashboardResponse dashboardResponse
    ) {

        FinancialHealthResponse response =
                new FinancialHealthResponse();
        BigDecimal savingsRate =
                dashboardResponse
                        .getFinancialOverview()
                        .getSavingsRate();
        int score = 0;
        if (savingsRate.compareTo(BigDecimal.valueOf(30)) >= 0) {

            score += 40;

        } else if (savingsRate.compareTo(BigDecimal.valueOf(20)) >= 0) {

            score += 30;

        } else if (savingsRate.compareTo(BigDecimal.valueOf(10)) >= 0) {

            score += 20;

        } else if (savingsRate.compareTo(BigDecimal.ZERO) > 0) {

            score += 10;

        }
        BudgetSummaryResponse budgetSummary =
                dashboardResponse.getBudgetSummary();
        int totalBudgets = budgetSummary.getTotalBudgets();
        int onTrack = budgetSummary.getOnTrack();
        BigDecimal budgetPercentage =
                BigDecimal.valueOf(onTrack)
                        .divide(
                                BigDecimal.valueOf(totalBudgets),
                                4,
                                RoundingMode.HALF_UP
                        )
                        .multiply(BigDecimal.valueOf(30));
        score += budgetPercentage.intValue();

        GoalSummaryResponse goalSummary =
                dashboardResponse.getGoalSummary();
        int totalGoals = goalSummary.getTotalGoals();

        int completedGoals = goalSummary.getCompletedGoals();
        if (totalGoals > 0) {

            BigDecimal goalPercentage =
                    BigDecimal.valueOf(completedGoals)
                            .divide(
                                    BigDecimal.valueOf(totalGoals),
                                    4,
                                    RoundingMode.HALF_UP
                            )
                            .multiply(BigDecimal.valueOf(20));

            score += goalPercentage.intValue();
        }
        PortfolioSummaryResponse portfolioSummary =
                dashboardResponse.getPortfolioSummary();

        if (portfolioSummary.getTotalInvested()
                .compareTo(BigDecimal.ZERO) > 0) {

            score += 10;
        }
        FinancialHealthStatus status;

        if (score >= 90) {

            status = FinancialHealthStatus.EXCELLENT;

        } else if (score >= 75) {

            status = FinancialHealthStatus.GOOD;

        } else if (score >= 50) {

            status = FinancialHealthStatus.FAIR;

        } else {

            status = FinancialHealthStatus.NEEDS_IMPROVEMENT;

        }
        response.setScore(score);
        response.setStatus(status);
        return response;
    }


}
