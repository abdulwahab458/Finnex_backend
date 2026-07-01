package com.finnex.finance_app.domain.portfolio.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public class UpdateHoldingRequest {
    @NotNull
    @Positive
    private int quantity;
    @NotNull
    @Positive
    private BigDecimal averageCostBasis;
}
