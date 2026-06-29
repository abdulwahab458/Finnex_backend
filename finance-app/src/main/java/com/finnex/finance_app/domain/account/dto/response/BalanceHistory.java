package com.finnex.finance_app.domain.account.dto.response;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class BalanceHistory {
    private LocalDate date;
    private BigDecimal balance;
    private String merchantName;
}
