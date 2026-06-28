package com.finnex.finance_app.domain.transactions.service;

import com.finnex.finance_app.common.response.PagedResponse;
import com.finnex.finance_app.domain.transactions.dto.request.CreateTransactionRequest;
import com.finnex.finance_app.domain.transactions.dto.request.UpdateTransactionRequest;
import com.finnex.finance_app.domain.transactions.dto.response.TransactionResponse;
import com.finnex.finance_app.domain.user.entity.User;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface TransactionService {
    TransactionResponse createTransaction(User user, CreateTransactionRequest request);
    PagedResponse<TransactionResponse> getTransactions(User user, Pageable pageable);
    TransactionResponse getTransactionById(User user,UUID transactionId);
    TransactionResponse updateTransaction(User user, UUID transactionId, UpdateTransactionRequest request);
    void deleteTransaction(User user,UUID transactionId);


}
