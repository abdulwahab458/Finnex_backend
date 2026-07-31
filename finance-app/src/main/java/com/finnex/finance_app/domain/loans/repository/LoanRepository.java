package com.finnex.finance_app.domain.loans.repository;

import com.finnex.finance_app.common.enums.LoanStatus;
import com.finnex.finance_app.domain.loans.entity.Loan;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface LoanRepository extends JpaRepository<Loan, UUID> {
    List<Loan> findByUserId(UUID userId);

    List<Loan> findByUserIdAndStatus(UUID userId, LoanStatus status);

    Optional<Loan> findByIdAndUserId(UUID loanId, UUID userId);
}
