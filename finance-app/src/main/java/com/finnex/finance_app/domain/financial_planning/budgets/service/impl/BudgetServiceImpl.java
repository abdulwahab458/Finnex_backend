package com.finnex.finance_app.domain.financial_planning.budgets.service.impl;

import com.finnex.finance_app.common.enums.BudgetStatus;
import com.finnex.finance_app.common.enums.TransactionCategory;
import com.finnex.finance_app.common.enums.TransactionType;
import com.finnex.finance_app.common.exceptions.BadRequestException;
import com.finnex.finance_app.common.exceptions.ResourceNotFound;
import com.finnex.finance_app.domain.financial_planning.budgets.dto.requet.CreateBudgetRequest;
import com.finnex.finance_app.domain.financial_planning.budgets.dto.requet.UpdateBudgetRequest;
import com.finnex.finance_app.domain.financial_planning.budgets.dto.response.BudgetResponse;
import com.finnex.finance_app.domain.financial_planning.budgets.entity.Budget;
import com.finnex.finance_app.domain.financial_planning.budgets.mapper.BudgetMapper;
import com.finnex.finance_app.domain.financial_planning.budgets.repository.BudgetRepository;
import com.finnex.finance_app.domain.financial_planning.budgets.service.BudgetService;
import com.finnex.finance_app.domain.transactions.entity.Transaction;
import com.finnex.finance_app.domain.transactions.repository.TransactionRepository;
import com.finnex.finance_app.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BudgetServiceImpl implements BudgetService {
    private final BudgetRepository budgetRepository;
    private final TransactionRepository transactionRepository;
    private final BudgetMapper budgetMapper;

    @Override
    @Transactional(readOnly = true)
    public List<BudgetResponse> getAllBudgets(User user) {
        List<Budget> budgets = budgetRepository.findByUserOrderByCreatedAtDesc(user);
        return budgets.stream()
                .map(this::buildBudgetResponse)
                .toList();
    }

    @Override
    @Transactional
    public BudgetResponse createBudget(User user, CreateBudgetRequest request) {
        boolean exists = budgetRepository.existsByUserAndCategoryAndStartDateAndEndDate(
                user,
                request.getCategory(),
                request.getStartDate(),
                request.getEndDate()
        );
        if (exists) {
            throw  new BadRequestException("Budget already exists");
        }

        Budget budget = budgetMapper.toEntity(request);
        budget.setUser(user);

        LocalDateTime startDateTime = request.getStartDate().atStartOfDay();

        LocalDateTime endDateTime = request.getEndDate().atTime(LocalTime.MAX);

        List<Transaction> transactions = transactionRepository.findByAccount_UserAndCategoryAndTransactionDateBetween(
                user,
                request.getCategory(),
                startDateTime,
                endDateTime
        );
        BigDecimal currentSpent = transactions.stream()
                .filter(transaction -> transaction.getType() == TransactionType.DEBIT)
                .map(Transaction::getAmount)
                .reduce(
                        BigDecimal.ZERO,
                        BigDecimal::add
                );
        budget.setCurrentSpent(currentSpent);
        budget = budgetRepository.save(budget);
        return buildBudgetResponse(budget);

    }

    @Override
    @Transactional
    public BudgetResponse updateBudget(User user, UUID budgetId, UpdateBudgetRequest request) {
        Budget budget = budgetRepository.findByIdAndUser(
                budgetId,
                user
        ).orElseThrow(
                () -> new ResourceNotFound("Budget with id " + budgetId + " not found")
        );


        boolean budgetKeyChanged =
                !budget.getCategory().equals(request.getCategory())
                        || !budget.getStartDate().equals(request.getStartDate())
                        || !budget.getEndDate().equals(request.getEndDate());

        if (budgetKeyChanged) {

            boolean exists =
                    budgetRepository.existsByUserAndCategoryAndStartDateAndEndDate(
                            user,
                            request.getCategory(),
                            request.getStartDate(),
                            request.getEndDate()
                    );

            if (exists) {
                throw new BadRequestException(
                        "Budget already exists for this category and period."
                );
            }
        }

        budget.setName(request.getName());
        budget.setCategory(request.getCategory());
        budget.setTargetAmount(request.getTargetAmount());
        budget.setPeriod(request.getPeriod());
        budget.setStartDate(request.getStartDate());
        budget.setEndDate(request.getEndDate());

        LocalDateTime startDateTime = request.getStartDate().atStartOfDay();

        LocalDateTime endDateTime = request.getEndDate().atTime(LocalTime.MAX);

        List<Transaction> transactions = transactionRepository.findByAccount_UserAndCategoryAndTransactionDateBetween(
                user,
                request.getCategory(),
                startDateTime,
                endDateTime
        );
        BigDecimal currentSpent = transactions.stream()
                .filter(transaction -> transaction.getType() == TransactionType.DEBIT)
                .map(Transaction::getAmount)
                .reduce(
                        BigDecimal.ZERO,
                        BigDecimal::add
                );
        budget.setCurrentSpent(currentSpent);
        budget = budgetRepository.save(budget);
        return buildBudgetResponse(budget);


    }

    @Override
    public void deleteBudget(User user, UUID budgetId) {
        Budget budget = budgetRepository.findByIdAndUser(
                budgetId,
                user
        ).orElseThrow(
                () -> new ResourceNotFound("Budget with id " + budgetId + " not found")
        );
        budgetRepository.delete(budget);

    }

    @Override
    @Transactional
    public void updateBudgetAfterTransactionCreate(Transaction transaction) {
        addTransactionToBudget(transaction);
    }

    @Override
    @Transactional
    public void updateBudgetAfterTransactionUpdate(Transaction oldTransaction, Transaction newTransaction) {
        removeTransactionFromBudget(oldTransaction);
        addTransactionToBudget(newTransaction);
    }

    @Override
    @Transactional
    public void updateBudgetAfterTransactionDelete(Transaction transaction) {
        removeTransactionFromBudget(transaction);
    }

    //helper function
    private BudgetResponse buildBudgetResponse(Budget budget) {
        BudgetResponse response = budgetMapper.toResponse(budget);
        response.setRemainingAmount(
                budget.getTargetAmount()
                        .subtract(
                                budget.getCurrentSpent()
                        )
        );

        BigDecimal progressPercentage;

        if (budget.getTargetAmount().compareTo(BigDecimal.ZERO) == 0) {
            progressPercentage = BigDecimal.ZERO;
        } else {
            progressPercentage =
                    budget.getCurrentSpent()
                            .divide(
                                    budget.getTargetAmount(),
                                    4,
                                    RoundingMode.HALF_UP
                            )
                            .multiply(
                                    BigDecimal.valueOf(100)

                            )
                            .setScale(2, RoundingMode.HALF_UP);
        }

        response.setProgressPercentage(progressPercentage);

        if (progressPercentage.compareTo(BigDecimal.valueOf(100)) >= 0) {
            response.setStatus(BudgetStatus.OVER_BUDGET);
        } else if (progressPercentage.compareTo(BigDecimal.valueOf(80)) >= 0) {
            response.setStatus(BudgetStatus.WARNING);
        } else {
            response.setStatus(BudgetStatus.ON_TRACK);
        }



        return response;
    }
    private void addTransactionToBudget(Transaction transaction){
        if (transaction.getType() != TransactionType.DEBIT) {
            return;
        }

        LocalDate transactionDate =
                transaction.getTransactionDate().toLocalDate();

        budgetRepository.findMatchingBudget(
                transaction.getAccount().getUser(),
                transaction.getCategory(),
                transactionDate
        ).ifPresent(budget -> {

            budget.setCurrentSpent(
                    budget.getCurrentSpent()
                            .add(transaction.getAmount())
            );

            budgetRepository.save(budget);

        });
    }
    private void removeTransactionFromBudget(Transaction transaction) {

        if (transaction.getType() != TransactionType.DEBIT) {
            return;
        }

        LocalDate transactionDate =
                transaction.getTransactionDate().toLocalDate();

        budgetRepository.findMatchingBudget(
                transaction.getAccount().getUser(),
                transaction.getCategory(),
                transactionDate
        ).ifPresent(budget -> {

            budget.setCurrentSpent(
                    budget.getCurrentSpent()
                            .subtract(transaction.getAmount())
            );

            budgetRepository.save(budget);

        });
    }
}
