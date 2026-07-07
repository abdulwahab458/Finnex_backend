package com.finnex.finance_app.domain.transactions.repository;

import com.finnex.finance_app.common.enums.TransactionCategory;
import com.finnex.finance_app.common.enums.TransactionStatus;
import com.finnex.finance_app.common.enums.TransactionType;
import com.finnex.finance_app.domain.account.entity.Account;
import com.finnex.finance_app.domain.transactions.entity.Transaction;
import com.finnex.finance_app.domain.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TransactionRepository extends JpaRepository<Transaction, UUID> {
     Page<Transaction> findByAccountUserId(UUID userId, Pageable pageable);
     List<Transaction> findByAccountUserId(UUID userId);
     Optional<Transaction> findByIdAndAccountUserId(UUID transactionId, UUID userId);
     Page<Transaction> findByAccount(Account account, Pageable pageable);
     Page<Transaction> findByAccountUserIdAndStatus(UUID id, TransactionStatus status, Pageable pageable);
     Page<Transaction> findByAccountUserIdAndCategory(UUID id, TransactionCategory category, Pageable pageable);
     Page<Transaction> findByAccountUserIdAndTransactionDateBetween(UUID id,LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);
     List<Transaction> findByAccountUserIdAndStatus(
             UUID userId, TransactionStatus status
     );
     List<Transaction> findByAccountUserIdAndCategory(
             UUID userId, TransactionCategory category
     );
     List<Transaction> findByAccountUserIdAndType(
             UUID userId,
             TransactionType type
     );
     List<Transaction> findByAccountOrderByTransactionDateAsc(Account account);

     List<Transaction> findByAccount_UserAndCategoryAndTransactionDateBetween(
             User user,
             TransactionCategory category,
             LocalDateTime startDate,
             LocalDateTime endDate
     );
}
