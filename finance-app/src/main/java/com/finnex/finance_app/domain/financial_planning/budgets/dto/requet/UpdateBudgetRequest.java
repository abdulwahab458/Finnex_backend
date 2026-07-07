package com.finnex.finance_app.domain.financial_planning.budgets.dto.requet;

import com.finnex.finance_app.common.enums.BudgetPeriod;
import com.finnex.finance_app.common.enums.TransactionCategory;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
@Data
public class UpdateBudgetRequest {
        @NotBlank(message = "Budget name is required")
        @Size(max = 100, message = "Budget name cannot exceed 100 characters")
        private String name;

        @NotNull(message = "Category is required")
        private TransactionCategory category;

        @NotNull(message = "Target amount is required")
        @DecimalMin(value = "0.01", message = "Target amount must be greater than zero")
        private BigDecimal targetAmount;

        @NotNull(message = "Budget period is required")
        private BudgetPeriod period;

        @NotNull(message = "Start date is required")
        private LocalDate startDate;

        @NotNull(message = "End date is required")
        private LocalDate endDate;
}
