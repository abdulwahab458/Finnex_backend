package com.finnex.finance_app.domain.transactions.service.impl;

import com.finnex.finance_app.common.enums.TransactionCategory;
import com.finnex.finance_app.common.enums.TransactionStatus;
import com.finnex.finance_app.common.enums.TransactionType;
import com.finnex.finance_app.common.exceptions.ResourceNotFound;
import com.finnex.finance_app.common.response.PagedResponse;
import com.finnex.finance_app.domain.account.Repository.AccountRepository;
import com.finnex.finance_app.domain.account.entity.Account;
import com.finnex.finance_app.domain.financial_planning.budgets.service.BudgetService;
import com.finnex.finance_app.domain.transactions.dto.request.CreateTransactionRequest;
import com.finnex.finance_app.domain.transactions.dto.request.UpdateTransactionRequest;
import com.finnex.finance_app.domain.transactions.dto.response.TransactionResponse;
import com.finnex.finance_app.domain.transactions.dto.response.TransactionSummaryResponse;
import com.finnex.finance_app.domain.transactions.entity.Transaction;
import com.finnex.finance_app.domain.transactions.export.TransactionExcelExporter;
import com.finnex.finance_app.domain.transactions.mapper.TransactionMapper;
import com.finnex.finance_app.domain.transactions.repository.TransactionRepository;
import com.finnex.finance_app.domain.transactions.service.TransactionService;
import com.finnex.finance_app.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class TransactionServiceImpl implements TransactionService {
    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;
    private final TransactionMapper transactionMapper;
    private final BudgetService budgetService;
    private  final TransactionExcelExporter transactionExcelExporter;
    @Override
    @Transactional
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
        applyTransaction(account,request.getType(),request.getAmount());

        account.setAvailableBalance(account.getBalance());
        accountRepository.save(account);
        transaction = transactionRepository.save(transaction);
        budgetService.updateBudgetAfterTransactionCreate(transaction);

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

    @Override
    @Transactional(readOnly = true)
    public TransactionResponse getTransactionById(User currentuser, UUID transactionId) {
        Transaction transaction = transactionRepository
                .findByIdAndAccountUserId(transactionId,currentuser.getId())
                .orElseThrow(() -> new ResourceNotFound("Transaction not found"));

        return transactionMapper.toResponse(transaction);
    }

    @Override
    @Transactional
    public TransactionResponse updateTransaction(User curretUser, UUID transactionId, UpdateTransactionRequest request) {
        Transaction transaction = transactionRepository
                .findByIdAndAccountUserId(transactionId,curretUser.getId())
                .orElseThrow(() -> new ResourceNotFound("Transaction not found"));

        Transaction oldTransaction = new Transaction();

        oldTransaction.setAccount(transaction.getAccount());
        oldTransaction.setAmount(transaction.getAmount());
        oldTransaction.setCategory(transaction.getCategory());
        oldTransaction.setType(transaction.getType());
        oldTransaction.setTransactionDate(transaction.getTransactionDate());

        Account account = transaction.getAccount();
        reverseTransaction(account,transaction.getType(),transaction.getAmount());
        //transaction Update
        transaction.setAmount(request.getAmount());
        transaction.setType(request.getType());
        transaction.setCategory(request.getCategory());
        transaction.setStatus(request.getStatus());
        transaction.setTransactionDate(request.getTransactionDate());
        transaction.setMerchantName(request.getMerchantName());
        transaction.setNotes(request.getNotes());

        // Apply new transaction
        switch (request.getType()) {

            case CREDIT ->
                    account.setBalance(
                            account.getBalance()
                                    .add(request.getAmount())
                    );

            case DEBIT ->
                    account.setBalance(
                            account.getBalance()
                                    .subtract(request.getAmount())
                    );

            case TRANSFER -> {
                // Later
            }
        }

        account.setAvailableBalance(account.getBalance());
        accountRepository.save(account);
        transaction = transactionRepository.save(transaction);
        budgetService.updateBudgetAfterTransactionUpdate(oldTransaction,transaction);
        return transactionMapper.toResponse(transaction);

    }

    @Override
    @Transactional
    public void deleteTransaction(User currentUser, UUID transactionId) {
        Transaction transaction = transactionRepository
                .findByIdAndAccountUserId(transactionId,currentUser.getId())
                .orElseThrow(() -> new ResourceNotFound("Transaction not found"));

        Account account = transaction.getAccount();
        reverseTransaction(account,transaction.getType(),transaction.getAmount());
        account.setAvailableBalance(account.getBalance());
        accountRepository.save(account);
        budgetService.updateBudgetAfterTransactionDelete(transaction);
        transactionRepository.delete(transaction);

    }

    @Override
    @Transactional(readOnly = true)
    public TransactionSummaryResponse getTransactionSummary(User currentUser) {
        List<Account> accounts = accountRepository.findByUser(currentUser);
        BigDecimal totalBalance = accounts
                .stream()
                .filter(Account::getActive)
                .map(Account::getBalance)
                .reduce(BigDecimal.ZERO, BigDecimal::add);


        List<Transaction> pendingTransactions = transactionRepository.findByAccountUserIdAndStatus(
                currentUser.getId(), TransactionStatus.PENDING
        );
        BigDecimal pendingAmount = pendingTransactions
                .stream()
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        long pendingCount = pendingTransactions.size();

        YearMonth currentMonth = YearMonth.now();

        BigDecimal monthlySpend =
                transactionRepository
                        .findByAccountUserIdAndType(
                                currentUser.getId(),
                                TransactionType.DEBIT
                        )
                        .stream()
                        .filter(transaction ->
                                YearMonth.from(
                                        transaction.getTransactionDate()
                                ).equals(currentMonth)
                        )
                        .map(Transaction::getAmount)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);


        BigDecimal dividends =
                transactionRepository
                        .findByAccountUserIdAndCategory(
                                currentUser.getId(),
                                TransactionCategory.DIVIDEND
                        )
                        .stream()
                        .map(Transaction::getAmount)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);

        TransactionSummaryResponse transactionSummaryResponse = new TransactionSummaryResponse();
        transactionSummaryResponse.setTotalBalance(totalBalance);
        transactionSummaryResponse.setPendingAmount(pendingAmount);
        transactionSummaryResponse.setPendingTransactions(pendingCount);
        transactionSummaryResponse.setMonthlySpend(monthlySpend);
        transactionSummaryResponse.setDividends(dividends);
        return transactionSummaryResponse;
    }

    @Override
    @Transactional(readOnly = true)
    public byte[] exportTransaction(User currentUser) throws IOException {
        List<Transaction> transactions = transactionRepository.findByAccountUserId(currentUser.getId());
        return  transactionExcelExporter.export(transactions);
    }

    //Utility functions
    private void applyTransaction(Account account, TransactionType type, BigDecimal amount){
        switch (type) {

            case CREDIT ->
                    account.setBalance(
                            account.getBalance().add(amount)
                    );

            case DEBIT ->
                    account.setBalance(
                            account.getBalance().subtract(amount)
                    );

            case TRANSFER -> {
                // Later
            }
        }

        account.setAvailableBalance(
                account.getBalance()
        );
    }

    private void reverseTransaction(Account account,TransactionType type,BigDecimal amount
    ) {

        switch (type) {

            case CREDIT ->
                    account.setBalance(
                            account.getBalance().subtract(amount)
                    );

            case DEBIT ->
                    account.setBalance(
                            account.getBalance().add(amount)
                    );

            case TRANSFER -> {
                // Later
            }
        }

        account.setAvailableBalance(
                account.getBalance()
        );
    }


}
