package com.finnex.finance_app.domain.portfolio.dto.reponse;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class PortfolioAllocationResponse {
    private String sector;
    private BigDecimal percentage;
}
