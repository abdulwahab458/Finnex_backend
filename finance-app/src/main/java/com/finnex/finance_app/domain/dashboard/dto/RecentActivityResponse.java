package com.finnex.finance_app.domain.dashboard.dto;

import com.finnex.finance_app.common.enums.TransactionCategory;
import com.finnex.finance_app.common.enums.TransactionType;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class RecentActivityResponse {

    private UUID transactionId;

    private String title;        // merchantName

    private TransactionCategory category;

    private TransactionType type;

    private BigDecimal amount;

    private LocalDateTime transactionDate;
}
