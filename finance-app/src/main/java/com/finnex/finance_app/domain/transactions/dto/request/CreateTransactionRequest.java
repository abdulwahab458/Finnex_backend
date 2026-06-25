package com.finnex.finance_app.domain.transactions.dto.request;

import com.finnex.finance_app.common.enums.TransactionCategory;
import com.finnex.finance_app.common.enums.TransactionStatus;
import com.finnex.finance_app.common.enums.TransactionType;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public class CreateTransactionRequest {
    @NotNull
    private UUID accountId;
    @NotNull
    private BigDecimal amount;
    @NotNull
    private TransactionType transactionType;
    @NotNull
    private TransactionStatus transactionStatus;
    @NotNull
    private TransactionCategory transactionCategory;
    @NotNull
    private LocalDateTime trasncationDate;
    @NotNull
    private  String notes;
    @NotNull
    private String merchantName;


}
