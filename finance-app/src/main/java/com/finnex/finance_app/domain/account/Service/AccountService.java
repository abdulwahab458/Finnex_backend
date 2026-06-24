package com.finnex.finance_app.domain.account.Service;

import com.finnex.finance_app.common.enums.AccountType;
import com.finnex.finance_app.domain.account.dto.request.CreateAccountRequest;
import com.finnex.finance_app.domain.account.dto.response.AccountResponse;
import com.finnex.finance_app.domain.user.entity.User;

import java.util.List;
import java.util.UUID;

public interface AccountService {
    AccountResponse createAccount(User currentUser, CreateAccountRequest request);
    List<AccountResponse> getAccounts(User currentUser, AccountType accountType);
    AccountResponse getAccountById(User currentUser, UUID id);
}
