package com.finnex.finance_app.domain.financial_planning.budgets.service;

import com.finnex.finance_app.domain.financial_planning.budgets.dto.requet.CreateBudgetRequest;
import com.finnex.finance_app.domain.financial_planning.budgets.dto.requet.UpdateBudgetRequest;
import com.finnex.finance_app.domain.financial_planning.budgets.dto.response.BudgetResponse;
import com.finnex.finance_app.domain.transactions.entity.Transaction;
import com.finnex.finance_app.domain.user.entity.User;

import java.util.List;
import java.util.UUID;

public interface BudgetService {
    List<BudgetResponse> getAllBudgets(User user);
    BudgetResponse createBudget(User user, CreateBudgetRequest request);
    BudgetResponse updateBudget(User user, UUID budgetId, UpdateBudgetRequest request);
    void  deleteBudget(User user, UUID budgetId);

    // Internal methods used by TransactionService
    void updateBudgetAfterTransactionCreate(Transaction  transaction);
    void updateBudgetAfterTransactionUpdate(
            Transaction oldTransaction,
            Transaction newTransaction
    );
    void  updateBudgetAfterTransactionDelete(
            Transaction  transaction
    );


}
