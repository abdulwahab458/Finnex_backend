package com.finnex.finance_app.domain.dashboard.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class PortfolioSummaryResponse {
    private BigDecimal totalInvested;

    private BigDecimal currentValue;

    private BigDecimal totalReturnPercentage;
}
