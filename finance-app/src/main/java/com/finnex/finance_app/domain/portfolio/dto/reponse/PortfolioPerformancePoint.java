package com.finnex.finance_app.domain.portfolio.dto.reponse;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PortfolioPerformancePoint {
    private LocalDate date;
    private BigDecimal portfolioValue;

}
