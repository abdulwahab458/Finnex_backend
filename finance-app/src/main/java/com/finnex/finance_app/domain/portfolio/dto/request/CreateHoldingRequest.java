package com.finnex.finance_app.domain.portfolio.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class CreateHoldingRequest {
    @NotBlank
    private String symbol;
    @NotNull
    @Positive
    private int quantity;
    @NotNull
    @Positive
    private BigDecimal averageCostBasis;
}
