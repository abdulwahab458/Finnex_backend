package com.finnex.finance_app.domain.intelligence.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccountSummary {

    private int totalAccounts;

    private BigDecimal totalBalance;

    private BigDecimal totalAvailableBalance;

    private List<AccountItem> accounts;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AccountItem {

        private String accountName;

        private String accountType;

        private BigDecimal balance;

        private BigDecimal availableBalance;

        private String currency;
    }
}
