package com.finnex.finance_app.domain.transactions.dto.response;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class TransactionSummaryResponse {

    private BigDecimal totalBalance;

    private BigDecimal pendingAmount;

    private long pendingTransactions;

    private BigDecimal monthlySpend;

    private BigDecimal dividends;
}
