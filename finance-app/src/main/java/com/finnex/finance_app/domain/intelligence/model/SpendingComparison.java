package com.finnex.finance_app.domain.intelligence.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SpendingComparison {

    private String currentPeriod;
    private String previousPeriod;

    private BigDecimal currentExpenses;
    private BigDecimal previousExpenses;

    private BigDecimal difference;

    private BigDecimal percentageChange;

    private String trend;
}