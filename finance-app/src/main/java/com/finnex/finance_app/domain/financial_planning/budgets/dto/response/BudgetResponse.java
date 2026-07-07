package com.finnex.finance_app.domain.financial_planning.budgets.dto.response;

import com.finnex.finance_app.common.enums.BudgetPeriod;
import com.finnex.finance_app.common.enums.BudgetStatus;
import com.finnex.finance_app.common.enums.TransactionCategory;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Data
public class BudgetResponse {

    private UUID id;

    private String name;

    private TransactionCategory category;

    private BigDecimal targetAmount;

    private BigDecimal currentSpent;

    private BigDecimal remainingAmount;

    private BigDecimal progressPercentage;

    private BudgetStatus status;

    private BudgetPeriod period;

    private LocalDate startDate;

    private LocalDate endDate;

}
