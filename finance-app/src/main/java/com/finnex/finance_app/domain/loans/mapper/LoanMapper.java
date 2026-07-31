package com.finnex.finance_app.domain.loans.mapper;

import com.finnex.finance_app.domain.loans.dto.request.CreateLoanRequest;
import com.finnex.finance_app.domain.loans.dto.response.LoanResponse;
import com.finnex.finance_app.domain.loans.entity.Loan;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface LoanMapper {
    
    Loan toEntity(CreateLoanRequest request);

    LoanResponse toResponse(Loan loan);

    List<LoanResponse> toResponseList(List<Loan> loans);
}
