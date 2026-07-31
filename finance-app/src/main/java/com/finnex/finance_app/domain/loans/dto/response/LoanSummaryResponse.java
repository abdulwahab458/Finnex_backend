package com.finnex.finance_app.domain.loans.dto.response;

import java.math.BigDecimal;

public class LoanSummaryResponse {
    private int totalLoans;

    private BigDecimal totalPrincipal;

    private BigDecimal totalOutstanding;

    private BigDecimal totalMonthlyEmi;
}
