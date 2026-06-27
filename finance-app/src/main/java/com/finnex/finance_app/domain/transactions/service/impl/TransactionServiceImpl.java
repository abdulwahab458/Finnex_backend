package com.finnex.finance_app.domain.transactions.service.impl;

import com.finnex.finance_app.common.exceptions.ResourceNotFound;
import com.finnex.finance_app.common.response.PagedResponse;
import com.finnex.finance_app.domain.account.Repository.AccountRepository;
import com.finnex.finance_app.domain.account.entity.Account;
import com.finnex.finance_app.domain.transactions.dto.request.CreateTransactionRequest;
import com.finnex.finance_app.domain.transactions.dto.response.TransactionResponse;
import com.finnex.finance_app.domain.transactions.entity.Transaction;
import com.finnex.finance_app.domain.transactions.mapper.TransactionMapper;
import com.finnex.finance_app.domain.transactions.repository.TransactionRepository;
import com.finnex.finance_app.domain.transactions.service.TransactionService;
import com.finnex.finance_app.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class TransactionServiceImpl implements TransactionService {
    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;
    private final TransactionMapper transactionMapper;
    @Override
    public TransactionResponse createTransaction(User user, CreateTransactionRequest request) {
        Account account = accountRepository
                .findByUserAndId(
                        user,
                        request.getAccountId()
                )
                .orElseThrow(() -> new ResourceNotFound("Account not found"));

        Transaction transaction = new Transaction();
        transaction.setAccount(account);
        transaction.setTransactionDate(request.getTransactionDate());
        transaction.setAmount(request.getAmount());
        transaction.setType(request.getType());
        transaction.setCategory(request.getCategory());
        transaction.setStatus(request.getStatus());
        transaction.setNotes(request.getNotes());
        transaction.setMerchantName(request.getMerchantName());
        switch (request.getType()) {
            case CREDIT:
                account.setBalance(account.getBalance().add(request.getAmount()));
                break;
            case DEBIT:
                account.setBalance(account.getBalance().subtract(request.getAmount()));
                break;
            case  TRANSFER:
                // for transfer we ll do it later
        }


        account.setAvailableBalance(account.getBalance());
        accountRepository.save(account);
        transaction = transactionRepository.save(transaction);

        return transactionMapper.toResponse(transaction);
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<TransactionResponse> getTransactions(
            User currentUser,
            Pageable pageable
    ) {

        Page<Transaction> transactions =
                transactionRepository.findByAccountUserId(
                        currentUser.getId(),
                        pageable
                );

        Page<TransactionResponse> response =
                transactions.map(transactionMapper::toResponse);

        return PagedResponse.of(response);
    }
}
