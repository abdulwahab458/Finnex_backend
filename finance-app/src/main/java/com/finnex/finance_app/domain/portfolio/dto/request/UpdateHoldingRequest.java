package com.finnex.finance_app.domain.portfolio.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;
@Data
public class UpdateHoldingRequest {
    @NotNull
    @Positive
    private int quantity;
    @NotNull
    @Positive
    private BigDecimal averageCostBasis;
}
