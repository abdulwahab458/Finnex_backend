package com.finnex.finance_app.domain.account.dto.response;

import com.finnex.finance_app.common.enums.AccountType;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Data
public class AccountResponse {
    private UUID id;
    private String accountName;
    private String maskedAccountNumber;
    private AccountType accountType;
    private BigDecimal balance;
    private BigDecimal availableBalance;
    private String currency;
    private Boolean active;
    private LocalDate openedDate;
}
