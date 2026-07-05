package com.finnex.finance_app.domain.portfolio.dto.reponse;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class PortfolioPerformanceResponse {
    private BigDecimal minPortfolioValue;

    private BigDecimal maxPortfolioValue;


    private List<PortfolioPerformancePoint> timeline;

}
