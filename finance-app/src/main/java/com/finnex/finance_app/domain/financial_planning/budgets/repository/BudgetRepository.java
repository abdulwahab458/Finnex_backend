package com.finnex.finance_app.domain.financial_planning.budgets.repository;

import com.finnex.finance_app.common.enums.TransactionCategory;
import com.finnex.finance_app.domain.financial_planning.budgets.entity.Budget;
import com.finnex.finance_app.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BudgetRepository extends JpaRepository<Budget, UUID> {
    List<Budget> findByUserOrderByCreatedAtDesc(User user);
    Optional<Budget> findByIdAndUser(UUID id, User user);
    @Query("""
    SELECT b
    FROM Budget b
    WHERE b.user = :user
      AND b.category = :category
      AND :transactionDate BETWEEN b.startDate AND b.endDate
""")
    Optional<Budget> findMatchingBudget(
            User user,
            TransactionCategory category,
            LocalDate transactionDate
    );

    boolean existsByUserAndCategoryAndStartDateAndEndDate(
            User user,
            TransactionCategory category,
            LocalDate startDate,
            LocalDate endDate
    );





}
