package com.finnex.finance_app.domain.dashboard.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
@Data
public class CashFlowPointResponse {
    private String period;
    private BigDecimal income;
    private BigDecimal expense;

}
