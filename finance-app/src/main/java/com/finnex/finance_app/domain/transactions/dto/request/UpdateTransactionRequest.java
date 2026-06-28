package com.finnex.finance_app.domain.transactions.dto.request;

import com.finnex.finance_app.common.enums.TransactionCategory;
import com.finnex.finance_app.common.enums.TransactionStatus;
import com.finnex.finance_app.common.enums.TransactionType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class UpdateTransactionRequest {

    @NotNull
    private UUID accountId;

    @NotNull
    @DecimalMin("0.01")
    private BigDecimal amount;

    @NotNull
    private TransactionType type;

    @NotNull
    private TransactionCategory category;

    @NotNull
    private TransactionStatus status;

    @NotNull
    private LocalDateTime transactionDate;

    private String merchantName;

    private String notes;
}