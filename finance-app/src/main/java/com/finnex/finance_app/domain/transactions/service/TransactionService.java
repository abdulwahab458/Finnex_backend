package com.finnex.finance_app.domain.transactions.service;

import com.finnex.finance_app.common.response.PagedResponse;
import com.finnex.finance_app.domain.transactions.dto.request.CreateTransactionRequest;
import com.finnex.finance_app.domain.transactions.dto.response.TransactionResponse;
import com.finnex.finance_app.domain.transactions.entity.Transaction;
import com.finnex.finance_app.domain.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface TransactionService {
    TransactionResponse createTransaction(User user, CreateTransactionRequest request);
    PagedResponse<TransactionResponse> getTransactions(User user, Pageable pageable);
}
