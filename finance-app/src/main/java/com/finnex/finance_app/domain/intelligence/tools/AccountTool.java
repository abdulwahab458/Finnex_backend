package com.finnex.finance_app.domain.intelligence.tools;

import com.finnex.finance_app.domain.account.Service.AccountService;
import com.finnex.finance_app.domain.account.dto.response.AccountResponse;
import com.finnex.finance_app.domain.intelligence.model.AccountSummary;
import com.finnex.finance_app.domain.intelligence.util.AiSecurityContext;
import com.finnex.finance_app.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Component
@RequiredArgsConstructor
public class AccountTool {

    private final AccountService accountService;
    private final AiSecurityContext aiSecurityContext;

    @Tool(description = """
            Get the authenticated user's financial account information.

            Use this tool when the user asks about:
            - how much money they have
            - their total account balance
            - available balance
            - their bank or financial accounts
            - savings account balance
            - checking account balance
            - investment account balance
            - account overview

            This tool returns only accounts belonging to the
            currently authenticated user.
            """)
    public AccountSummary getAccountSummary() {

        User currentUser =
                aiSecurityContext.getCurrentUser();

        List<AccountResponse> accounts =
                accountService.getAccounts(
                        currentUser,
                        null
                );

        BigDecimal totalBalance =
                accounts.stream()
                        .map(AccountResponse::getBalance)
                        .filter(balance -> balance != null)
                        .reduce(
                                BigDecimal.ZERO,
                                BigDecimal::add
                        );

        BigDecimal totalAvailableBalance =
                accounts.stream()
                        .map(AccountResponse::getAvailableBalance)
                        .filter(balance -> balance != null)
                        .reduce(
                                BigDecimal.ZERO,
                                BigDecimal::add
                        );

        List<AccountSummary.AccountItem> accountItems =
                accounts.stream()
                        .map(account ->
                                AccountSummary.AccountItem
                                        .builder()
                                        .accountName(
                                                account.getAccountName()
                                        )
                                        .accountType(
                                                account.getAccountType() != null
                                                        ? account.getAccountType().name()
                                                        : null
                                        )
                                        .balance(
                                                account.getBalance()
                                        )
                                        .availableBalance(
                                                account.getAvailableBalance()
                                        )
                                        .currency(
                                                account.getCurrency()
                                        )
                                        .build()
                        )
                        .toList();

        return AccountSummary.builder()
                .totalAccounts(accounts.size())
                .totalBalance(totalBalance)
                .totalAvailableBalance(
                        totalAvailableBalance
                )
                .accounts(accountItems)
                .build();
    }
}
