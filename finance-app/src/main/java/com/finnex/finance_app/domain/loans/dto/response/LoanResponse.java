package com.finnex.finance_app.domain.loans.dto.response;

import com.finnex.finance_app.common.enums.LoanStatus;
import com.finnex.finance_app.common.enums.LoanType;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;
@Data
public class LoanResponse {
    private UUID id;

    private String loanName;

    private LoanType loanType;

    private String lenderName;

    private BigDecimal principalAmount;

    private BigDecimal outstandingBalance;

    private BigDecimal interestRate;

    private BigDecimal emiAmount;

    private LocalDate startDate;

    private LocalDate endDate;

    private LoanStatus status;
}
