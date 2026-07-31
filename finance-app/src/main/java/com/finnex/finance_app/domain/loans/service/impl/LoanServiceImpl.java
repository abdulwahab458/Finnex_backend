package com.finnex.finance_app.domain.loans.service.impl;

import com.finnex.finance_app.common.enums.LoanStatus;
import com.finnex.finance_app.common.exceptions.BadRequestException;
import com.finnex.finance_app.common.exceptions.ResourceNotFound;
import com.finnex.finance_app.domain.loans.dto.request.CreateLoanRequest;
import com.finnex.finance_app.domain.loans.dto.request.RecordPaymentRequest;
import com.finnex.finance_app.domain.loans.dto.request.UpdateLoanRequest;
import com.finnex.finance_app.domain.loans.dto.response.LoanResponse;
import com.finnex.finance_app.domain.loans.entity.Loan;
import com.finnex.finance_app.domain.loans.mapper.LoanMapper;
import com.finnex.finance_app.domain.loans.repository.LoanRepository;
import com.finnex.finance_app.domain.loans.service.LoanService;
import com.finnex.finance_app.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class LoanServiceImpl implements LoanService {
    private final LoanRepository loanRepository;
    private final LoanMapper loanMapper;
    @Override
    public List<LoanResponse> getAllLoans(User user) {

        List<Loan> loans = loanRepository.findByUserId(user.getId());

        return loanMapper.toResponseList(loans);
    }

    @Override
    public LoanResponse getLoanById(User user, UUID loanId) {
        Loan loan = loanRepository.findByIdAndUserId(loanId, user.getId())
                .orElseThrow(() -> new ResourceNotFound(
                        "Loan not found."
                ));

        return loanMapper.toResponse(loan);
    }

    @Override
    public LoanResponse createLoan(User user, CreateLoanRequest request) {
        if (request.getEndDate().isBefore(request.getStartDate())) {
            throw new BadRequestException(
                    "End date must be after start date."
            );
        }
        Loan loan = loanMapper.toEntity(request);

        loan.setUser(user);

        loan.setOutstandingBalance(
                request.getPrincipalAmount()
        );

        loan.setStatus(
                LoanStatus.ACTIVE
        );


        Loan savedLoan = loanRepository.save(loan);

        return loanMapper.toResponse(savedLoan);
    }

    @Override
    public LoanResponse updateLoan(User user, UUID loanId, UpdateLoanRequest request) {
        Loan loan = loanRepository.findByIdAndUserId(loanId, user.getId())
                .orElseThrow(() -> new ResourceNotFound(
                        "Loan not found."
                ));

        if (request.getEndDate().isBefore(request.getStartDate())) {
            throw new BadRequestException(
                    "End date must be after start date."
            );
        }

        loan.setLoanName(request.getLoanName());
        loan.setLoanType(request.getLoanType());
        loan.setLenderName(request.getLenderName());
        loan.setInterestRate(request.getInterestRate());
        loan.setEmiAmount(request.getEmiAmount());
        loan.setStartDate(request.getStartDate());
        loan.setEndDate(request.getEndDate());

        Loan updatedLoan = loanRepository.save(loan);

        return loanMapper.toResponse(updatedLoan);
    }

    @Override
    public void deleteLoan(User user, UUID loanId) {
        Loan loan = loanRepository.findByIdAndUserId(loanId, user.getId())
                .orElseThrow(() -> new ResourceNotFound(
                        "Loan not found."
                ));

        loanRepository.delete(loan);
    }

    @Override
    public LoanResponse recordPayment(User user, UUID loanId, RecordPaymentRequest request) {
        Loan loan = loanRepository.findByIdAndUserId(loanId, user.getId())
                .orElseThrow(() -> new ResourceNotFound(
                        "Loan not found."
                ));

        if (request.getAmount().compareTo(loan.getEmiAmount()) < 0) {
            throw new BadRequestException(
                    "Payment amount cannot be less than the EMI amount."
            );
        }

        if (request.getAmount().compareTo(loan.getOutstandingBalance()) > 0) {
            throw new BadRequestException(
                    "Payment amount cannot exceed the outstanding balance."
            );
        }

        BigDecimal remainingBalance = loan.getOutstandingBalance()
                .subtract(request.getAmount());

        loan.setOutstandingBalance(remainingBalance);

        if (remainingBalance.compareTo(BigDecimal.ZERO) == 0) {
            loan.setStatus(LoanStatus.CLOSED);
        }

        Loan updatedLoan = loanRepository.save(loan);

        return loanMapper.toResponse(updatedLoan);
    }
}
