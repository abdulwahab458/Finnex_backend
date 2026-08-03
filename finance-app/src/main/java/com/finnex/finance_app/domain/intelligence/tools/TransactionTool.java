package com.finnex.finance_app.domain.intelligence.tools;

import com.finnex.finance_app.common.enums.TransactionCategory;
import com.finnex.finance_app.common.enums.TransactionStatus;
import com.finnex.finance_app.common.enums.TransactionType;
import com.finnex.finance_app.domain.intelligence.model.*;
import com.finnex.finance_app.domain.intelligence.util.AiSecurityContext;
import com.finnex.finance_app.domain.transactions.dto.response.TransactionResponse;
import com.finnex.finance_app.domain.transactions.entity.Transaction;
import com.finnex.finance_app.domain.transactions.repository.TransactionRepository;
import com.finnex.finance_app.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class TransactionTool {

    private final TransactionRepository transactionRepository;
    private final AiSecurityContext aiSecurityContext;

    @Tool(description = """
            Analyze the authenticated user's income and expenses
            for the current month.
            
            Use this tool when the user asks:
            - how much they spent this month
            - how much income they received this month
            - where their money went
            - their biggest spending categories
            - their monthly cash flow
            - what category they spend the most on
            
            Only completed transactions are included.
            """)
    public TransactionAnalysis getCurrentMonthAnalysis() {

        User currentUser =
                aiSecurityContext.getCurrentUser();

        YearMonth month = YearMonth.now();

        LocalDateTime start =
                month.atDay(1).atStartOfDay();

        LocalDateTime end =
                month.plusMonths(1)
                        .atDay(1)
                        .atStartOfDay();

        return buildAnalysis(
                currentUser,
                month,
                start,
                end
        );
    }

    @Tool(description = """
            Compare the authenticated user's spending this month
            with the previous month.
            
            Use this tool when the user asks whether their spending
            increased or decreased, or asks to compare this month's
            expenses with last month.
            """)
    public SpendingComparison compareCurrentMonthWithPreviousMonth() {

        User currentUser =
                aiSecurityContext.getCurrentUser();

        YearMonth currentMonth = YearMonth.now();
        YearMonth previousMonth =
                currentMonth.minusMonths(1);

        BigDecimal currentExpenses =
                getExpensesForMonth(
                        currentUser,
                        currentMonth
                );

        BigDecimal previousExpenses =
                getExpensesForMonth(
                        currentUser,
                        previousMonth
                );

        BigDecimal difference =
                currentExpenses.subtract(
                        previousExpenses
                );

        BigDecimal percentageChange =
                calculatePercentageChange(
                        previousExpenses,
                        currentExpenses
                );

        String trend;

        int comparison =
                currentExpenses.compareTo(
                        previousExpenses
                );

        if (comparison > 0) {
            trend = "INCREASED";
        } else if (comparison < 0) {
            trend = "DECREASED";
        } else {
            trend = "UNCHANGED";
        }

        return SpendingComparison.builder()
                .currentPeriod(currentMonth.toString())
                .previousPeriod(previousMonth.toString())
                .currentExpenses(currentExpenses)
                .previousExpenses(previousExpenses)
                .difference(difference)
                .percentageChange(percentageChange)
                .trend(trend)
                .build();
    }

    @Tool(description = """
            Analyze the authenticated user's income, expenses,
            cash flow, and top spending categories for a specific month.
            
            Use this tool when the user asks about financial activity
            for a particular month such as:
            - How much did I spend in June?
            - What was my income in March?
            - Where did my money go in July?
            - How was my cash flow in January?
            
            The month must be provided in YYYY-MM format.
            """)
    public TransactionAnalysis getMonthlyAnalysis(
            MonthlyTransactionQuery query
    ) {

        User currentUser =
                aiSecurityContext.getCurrentUser();

        YearMonth month;

        try {
            month = YearMonth.parse(query.getMonth());
        } catch (Exception exception) {
            throw new IllegalArgumentException(
                    "Invalid month. Expected YYYY-MM format."
            );
        }

        LocalDateTime start =
                month.atDay(1).atStartOfDay();

        LocalDateTime end =
                month.plusMonths(1)
                        .atDay(1)
                        .atStartOfDay();

        return buildAnalysis(
                currentUser,
                month,
                start,
                end
        );
    }

    @Tool(description = """
            Analyze how much the authenticated user spent in a specific
            transaction category during a specific month.
            
            Use this tool for questions such as:
            - How much did I spend on food in June?
            - What did I spend on shopping in July?
            - How much went to entertainment in March?
            - What percentage of my expenses was groceries in January?
            
            The category must match a supported transaction category.
            The month must use YYYY-MM format.
            """)
    public CategorySpendingAnalysis getCategorySpending(
            CategorySpendingQuery query
    ) {

        User currentUser =
                aiSecurityContext.getCurrentUser();

        YearMonth month;

        try {
            month = YearMonth.parse(query.getMonth());
        } catch (Exception exception) {
            throw new IllegalArgumentException(
                    "Invalid month. Expected YYYY-MM format."
            );
        }

        TransactionCategory category;

        try {
            category = TransactionCategory.valueOf(
                    query.getCategory().toUpperCase()
            );
        } catch (Exception exception) {
            throw new IllegalArgumentException(
                    "Unsupported transaction category: "
                            + query.getCategory()
            );
        }

        LocalDateTime start =
                month.atDay(1)
                        .atStartOfDay();

        LocalDateTime end =
                month.plusMonths(1)
                        .atDay(1)
                        .atStartOfDay();

        List<Transaction> categoryExpenses =
                transactionRepository
                        .findByAccount_UserAndCategoryAndTransactionDateBetween(
                                currentUser,
                                category,
                                start,
                                end
                        )
                        .stream()
                        .filter(transaction ->
                                transaction.getType()
                                        == TransactionType.DEBIT
                        )
                        .filter(transaction ->
                                transaction.getStatus()
                                        == TransactionStatus.COMPLETED
                        )
                        .toList();

        BigDecimal categoryTotal =
                sumTransactions(categoryExpenses);

        List<Transaction> allExpenses =
                transactionRepository
                        .findByAccountUserIdAndTypeAndStatusAndTransactionDateBetween(
                                currentUser.getId(),
                                TransactionType.DEBIT,
                                TransactionStatus.COMPLETED,
                                start,
                                end
                        );

        BigDecimal totalExpenses =
                sumTransactions(allExpenses);

        BigDecimal percentage =
                calculatePercentage(
                        categoryTotal,
                        totalExpenses
                );

        return CategorySpendingAnalysis.builder()
                .period(month.toString())
                .category(category.name())
                .amount(categoryTotal)
                .transactionCount(
                        categoryExpenses.size()
                )
                .percentageOfTotalExpenses(
                        percentage
                )
                .build();
    }


    @Tool(description = """
            Get all transactions belonging to a specific category
            for the authenticated user.
            
            Use this tool when the user asks whether they have
            transactions in a particular category.
            
            Examples:
            - Do I have any salary transactions?
            - Are there transactions for category salary?
            - Show my dividend transactions.
            - Do I have any rent transactions?
            - Show my food transactions.
            
            This tool searches all dates and all transaction types.
            It is NOT limited to debit transactions.
            
            Do not use this tool when the user specifically asks
            how much they spent in a category for a particular month.
            """)
    public List<TransactionResponse> getTransactionsByCategory(
            @ToolParam(description = """
                    Transaction category.
                    Valid examples include:
                    SALARY, FOOD_AND_DINING, SHOPPING, GROCERIES,
                    TRANSPORT, HEALTHCARE, INSURANCE, UTILITIES,
                    ENTERTAINMENT, INVESTMENT, DIVIDEND, TRANSFER,
                    EDUCATION, RENT, TAX, OTHER.
                    """)
            String category
    ) {

        User currentUser =
                aiSecurityContext.getCurrentUser();

        TransactionCategory transactionCategory;

        try {

            transactionCategory =
                    TransactionCategory.valueOf(
                            category
                                    .trim()
                                    .toUpperCase()
                    );

        } catch (IllegalArgumentException exception) {

            throw new IllegalArgumentException(
                    "Unsupported transaction category: "
                            + category
            );
        }

        return transactionRepository
                .findByAccountUserIdAndCategory(
                        currentUser.getId(),
                        transactionCategory
                )
                .stream()
                .map(transaction -> {

                    TransactionResponse response =
                            new TransactionResponse();

                    response.setId(
                            transaction.getId()
                    );

                    response.setAccountId(
                            transaction
                                    .getAccount()
                                    .getId()
                    );

                    response.setAmount(
                            transaction.getAmount()
                    );

                    response.setType(
                            transaction.getType()
                    );

                    response.setCategory(
                            transaction.getCategory()
                    );

                    response.setStatus(
                            transaction.getStatus()
                    );

                    response.setTransactionDate(
                            transaction.getTransactionDate()
                    );

                    response.setMerchantName(
                            transaction.getMerchantName()
                    );

                    response.setNotes(
                            transaction.getNotes()
                    );

                    return response;
                })
                .toList();
    }


    @Tool(description = """
        Get transactions belonging to a specific category
        for a specific month.

        Use this tool when the user asks whether transactions
        exist in a category during a particular month.

        Examples:
        - Are there salary transactions in July 2026?
        - Show my salary transactions for July 2026.
        - Did I receive any salary in June 2026?
        - Were there dividend transactions in May 2026?
        - Show my rent transactions for July 2026.

        This tool includes BOTH credit and debit transactions.

        Do NOT use the category spending tool unless the user
        specifically asks how much they SPENT or their EXPENSES
        in a category.
        """)
    public List<TransactionResponse> getTransactionsByCategoryAndMonth(

            @ToolParam(description = """
                Transaction category such as SALARY,
                FOOD_AND_DINING, SHOPPING, RENT, DIVIDEND,
                TRANSPORT, GROCERIES, or UTILITIES.
                """)
            String category,

            @ToolParam(description = """
                Month in YYYY-MM format.
                Example: July 2026 = 2026-07.
                """)
            String month
    ) {

        User currentUser =
                aiSecurityContext.getCurrentUser();

        TransactionCategory transactionCategory;

        try {

            transactionCategory =
                    TransactionCategory.valueOf(
                            category.trim().toUpperCase()
                    );

        } catch (IllegalArgumentException exception) {

            throw new IllegalArgumentException(
                    "Unsupported transaction category: "
                            + category
            );
        }

        YearMonth yearMonth;

        try {

            yearMonth = YearMonth.parse(month);

        } catch (Exception exception) {

            throw new IllegalArgumentException(
                    "Invalid month. Expected YYYY-MM format."
            );
        }

        LocalDateTime start =
                yearMonth.atDay(1)
                        .atStartOfDay();

        LocalDateTime end =
                yearMonth.plusMonths(1)
                        .atDay(1)
                        .atStartOfDay();

        return transactionRepository
                .findByAccount_UserAndCategoryAndTransactionDateBetween(
                        currentUser,
                        transactionCategory,
                        start,
                        end
                )
                .stream()
                .map(transaction -> {

                    TransactionResponse response =
                            new TransactionResponse();

                    response.setId(transaction.getId());

                    response.setAccountId(
                            transaction.getAccount().getId()
                    );

                    response.setAmount(
                            transaction.getAmount()
                    );

                    response.setType(
                            transaction.getType()
                    );

                    response.setCategory(
                            transaction.getCategory()
                    );

                    response.setStatus(
                            transaction.getStatus()
                    );

                    response.setTransactionDate(
                            transaction.getTransactionDate()
                    );

                    response.setMerchantName(
                            transaction.getMerchantName()
                    );

                    response.setNotes(
                            transaction.getNotes()
                    );

                    return response;
                })
                .toList();
    }


    private BigDecimal getCategoryExpenses(
            User user,
            TransactionCategory category,
            YearMonth month
    ) {

        LocalDateTime start =
                month.atDay(1)
                        .atStartOfDay();

        LocalDateTime end =
                month.plusMonths(1)
                        .atDay(1)
                        .atStartOfDay();

        return transactionRepository
                .findByAccount_UserAndCategoryAndTransactionDateBetween(
                        user,
                        category,
                        start,
                        end
                )
                .stream()
                .filter(transaction ->
                        transaction.getType()
                                == TransactionType.DEBIT
                )
                .filter(transaction ->
                        transaction.getStatus()
                                == TransactionStatus.COMPLETED
                )
                .map(Transaction::getAmount)
                .reduce(
                        BigDecimal.ZERO,
                        BigDecimal::add
                );
    }

    private TransactionAnalysis buildAnalysis(
            User user,
            YearMonth month,
            LocalDateTime start,
            LocalDateTime end
    ) {

        List<Transaction> expenses =
                transactionRepository
                        .findByAccountUserIdAndTypeAndStatusAndTransactionDateBetween(
                                user.getId(),
                                TransactionType.DEBIT,
                                TransactionStatus.COMPLETED,
                                start,
                                end
                        );

        List<Transaction> income =
                transactionRepository
                        .findByAccountUserIdAndTypeAndStatusAndTransactionDateBetween(
                                user.getId(),
                                TransactionType.CREDIT,
                                TransactionStatus.COMPLETED,
                                start,
                                end
                        );

        BigDecimal totalExpenses =
                sumTransactions(expenses);

        BigDecimal totalIncome =
                sumTransactions(income);

        BigDecimal netCashFlow =
                totalIncome.subtract(totalExpenses);

        List<TransactionAnalysis.CategorySpending>
                categories =
                buildCategoryBreakdown(
                        expenses,
                        totalExpenses
                );

        return TransactionAnalysis.builder()
                .period(month.toString())
                .totalIncome(totalIncome)
                .totalExpenses(totalExpenses)
                .netCashFlow(netCashFlow)
                .transactionCount(
                        expenses.size() + income.size()
                )
                .topSpendingCategories(categories)
                .build();
    }

    private BigDecimal getExpensesForMonth(
            User user,
            YearMonth month
    ) {

        LocalDateTime start =
                month.atDay(1).atStartOfDay();

        LocalDateTime end =
                month.plusMonths(1)
                        .atDay(1)
                        .atStartOfDay();

        List<Transaction> transactions =
                transactionRepository
                        .findByAccountUserIdAndTypeAndStatusAndTransactionDateBetween(
                                user.getId(),
                                TransactionType.DEBIT,
                                TransactionStatus.COMPLETED,
                                start,
                                end
                        );

        return sumTransactions(transactions);
    }

    private BigDecimal sumTransactions(
            List<Transaction> transactions
    ) {

        return transactions.stream()
                .map(Transaction::getAmount)
                .filter(amount -> amount != null)
                .reduce(
                        BigDecimal.ZERO,
                        BigDecimal::add
                );
    }

    private List<TransactionAnalysis.CategorySpending>
    buildCategoryBreakdown(
            List<Transaction> expenses,
            BigDecimal totalExpenses
    ) {

        Map<String, BigDecimal> categoryTotals =
                expenses.stream()
                        .collect(
                                Collectors.groupingBy(
                                        transaction ->
                                                transaction.getCategory() != null
                                                        ? transaction.getCategory().name()
                                                        : "UNCATEGORIZED",
                                        Collectors.reducing(
                                                BigDecimal.ZERO,
                                                Transaction::getAmount,
                                                BigDecimal::add
                                        )
                                )
                        );

        return categoryTotals
                .entrySet()
                .stream()
                .map(entry -> {

                    BigDecimal percentage =
                            calculatePercentage(
                                    entry.getValue(),
                                    totalExpenses
                            );

                    return TransactionAnalysis
                            .CategorySpending
                            .builder()
                            .category(entry.getKey())
                            .amount(entry.getValue())
                            .percentage(percentage)
                            .build();
                })
                .sorted(
                        Comparator.comparing(
                                TransactionAnalysis
                                        .CategorySpending::getAmount
                        ).reversed()
                )
                .limit(5)
                .toList();
    }

    private BigDecimal calculatePercentage(
            BigDecimal amount,
            BigDecimal total
    ) {

        if (total.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }

        return amount
                .multiply(BigDecimal.valueOf(100))
                .divide(
                        total,
                        2,
                        RoundingMode.HALF_UP
                );
    }

    private BigDecimal calculatePercentageChange(
            BigDecimal previous,
            BigDecimal current
    ) {

        if (previous.compareTo(BigDecimal.ZERO) == 0) {

            if (current.compareTo(BigDecimal.ZERO) == 0) {
                return BigDecimal.ZERO;
            }

            // Percentage growth from zero is undefined.
            return null;
        }

        return current
                .subtract(previous)
                .multiply(BigDecimal.valueOf(100))
                .divide(
                        previous,
                        2,
                        RoundingMode.HALF_UP
                );
    }
}