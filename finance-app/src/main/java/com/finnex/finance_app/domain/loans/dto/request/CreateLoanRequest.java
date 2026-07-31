package com.finnex.finance_app.domain.loans.dto.request;

import com.finnex.finance_app.common.enums.LoanType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
@Data
public class CreateLoanRequest {
    @NotBlank
    private String loanName;

    @NotNull
    private LoanType loanType;

    @NotBlank
    private String lenderName;

    @NotNull
    @Positive
    private BigDecimal principalAmount;

    @NotNull
    @DecimalMin("0.0")
    private BigDecimal interestRate;

    @NotNull
    @Positive
    private BigDecimal emiAmount;

    @NotNull
    private LocalDate startDate;

    @NotNull
    private LocalDate endDate;
}
