package com.finnex.finance_app.domain.financial_planning.budgets.mapper;

import com.finnex.finance_app.domain.financial_planning.budgets.dto.requet.CreateBudgetRequest;
import com.finnex.finance_app.domain.financial_planning.budgets.dto.response.BudgetResponse;
import com.finnex.finance_app.domain.financial_planning.budgets.entity.Budget;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface BudgetMapper {
    Budget toEntity(CreateBudgetRequest request);
    BudgetResponse  toResponse(Budget budget);
}
