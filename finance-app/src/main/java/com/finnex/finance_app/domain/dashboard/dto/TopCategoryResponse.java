package com.finnex.finance_app.domain.dashboard.dto;

import com.finnex.finance_app.common.enums.TransactionCategory;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class TopCategoryResponse {
    private TransactionCategory category;
    private BigDecimal amount;
}
