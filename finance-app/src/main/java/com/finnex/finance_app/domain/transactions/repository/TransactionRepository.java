package com.finnex.finance_app.domain.transactions.repository;

import com.finnex.finance_app.domain.transactions.entity.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface TransactionRepository extends JpaRepository<Transaction, UUID> {
     Page<Transaction> findByAccountUserId(UUID userId, Pageable pageable);
     Optional<Transaction> findByIdAndAccountUserId(UUID id, UUID userId);

}
