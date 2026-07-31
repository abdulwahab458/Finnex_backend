package com.finnex.finance_app.domain.loans.service;

import com.finnex.finance_app.domain.loans.dto.request.CreateLoanRequest;
import com.finnex.finance_app.domain.loans.dto.request.RecordPaymentRequest;
import com.finnex.finance_app.domain.loans.dto.request.UpdateLoanRequest;
import com.finnex.finance_app.domain.loans.dto.response.LoanResponse;
import com.finnex.finance_app.domain.user.entity.User;

import java.util.List;
import java.util.UUID;

public interface LoanService {
    List<LoanResponse> getAllLoans(User user);

    LoanResponse getLoanById(User user, UUID loanId);

    LoanResponse createLoan(User user, CreateLoanRequest request);

    LoanResponse updateLoan(User user, UUID loanId, UpdateLoanRequest request);

    void deleteLoan(User user, UUID loanId);

    LoanResponse recordPayment(User user, UUID loanId, RecordPaymentRequest request);
}
