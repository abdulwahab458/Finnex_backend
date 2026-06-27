package com.finnex.finance_app.domain.transactions.dto.response;

import com.finnex.finance_app.common.enums.TransactionCategory;
import com.finnex.finance_app.common.enums.TransactionStatus;
import com.finnex.finance_app.common.enums.TransactionType;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;
@Data
public class TransactionResponse {
    private UUID id;
    private UUID accountId;

    private BigDecimal amount;

    private TransactionType type;

    private TransactionCategory category;

    private TransactionStatus status;

    private LocalDateTime transactionDate;

    private String merchantName;

    private String notes;

    private String attachmentUrl;

}
