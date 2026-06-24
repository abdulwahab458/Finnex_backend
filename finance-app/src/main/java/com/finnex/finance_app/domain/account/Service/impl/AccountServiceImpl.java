package com.finnex.finance_app.domain.account.Service.impl;

import com.finnex.finance_app.common.enums.AccountType;
import com.finnex.finance_app.common.exceptions.BadRequestException;
import com.finnex.finance_app.common.exceptions.DuplicateResourceException;
import com.finnex.finance_app.common.exceptions.ResourceNotFound;
import com.finnex.finance_app.domain.account.Repository.AccountRepository;
import com.finnex.finance_app.domain.account.Service.AccountService;
import com.finnex.finance_app.domain.account.dto.request.CreateAccountRequest;
import com.finnex.finance_app.domain.account.dto.request.UpdateAccountRequest;
import com.finnex.finance_app.domain.account.dto.response.AccountResponse;
import com.finnex.finance_app.domain.account.entity.Account;
import com.finnex.finance_app.domain.account.mapper.AccountMapper;
import com.finnex.finance_app.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {
    private  final AccountRepository accountRepository;
    private  final AccountMapper accountMapper;


    @Override
    public AccountResponse createAccount(User currentUser, CreateAccountRequest request) {
        if(accountRepository.existsByAccountNumber(request.getAccountNumber())){
            throw new DuplicateResourceException("Account Already exists");
        }
        Account account = new Account();
        account.setUser(currentUser);
        account.setAccountName(request.getAccountName());
        account.setAccountNumber(request.getAccountNumber());
        account.setAccountType(request.getAccountType());
        account.setCurrency(request.getCurrency());
        account.setBalance(request.getInitialBalance());
        account.setAvailableBalance(request.getInitialBalance());
        account.setOpenedDate(
                LocalDate.now()
        );
        account.setActive(true);
        accountRepository.save(account);
        return accountMapper.toResponse(account);
    }

    @Override
    public List<AccountResponse> getAccounts(
            User currentUser,
            AccountType accountType
    ) {

        List<Account> accounts;

        if (accountType != null) {
            accounts = accountRepository.findByUserAndAccountType(
                    currentUser,
                    accountType
            );
        } else {
            accounts = accountRepository.findByUser(
                    currentUser
            );
        }

        return accounts.stream()
                .map(accountMapper::toResponse)
                .toList();
    }

    @Override
    public AccountResponse getAccountById(User currentUser, UUID id) {
        Account account = accountRepository.findByUserAndId(currentUser,id).orElseThrow(()->new ResourceNotFound("Account Not Found"));
        return accountMapper.toResponse(account);
    }

    @Override
    public AccountResponse updateAccount(User currentUser, UUID id, UpdateAccountRequest request) {
        Account account = accountRepository.findByUserAndId(currentUser,id).orElseThrow(()->new ResourceNotFound("Account Not Found"));
        account.setAccountName(request.getAccountName());
        accountRepository.save(account);
        return accountMapper.toResponse(account);

    }

    @Override
    public void deActivateAccount(User currentUser, UUID id) {
        Account account = accountRepository.findByUserAndId(currentUser,id).orElseThrow(()->new ResourceNotFound("Account Not Found"));
        if(account.getBalance().compareTo(BigDecimal.ZERO)>0){
            throw  new BadRequestException("Transfer Funds to deactivate Account");
        }
        account.setActive(false);
        account.setClosedDate(LocalDate.now());
        accountRepository.save(account);
    }


}
