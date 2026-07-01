package com.finnex.finance_app.domain.portfolio.dto.reponse;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class PortfolioPeformanceResponse {
    private LocalDate date;
    private BigDecimal portfolioValue;

}
