package com.finnex.finance_app.domain.portfolio.dto.request;

import com.finnex.finance_app.common.enums.RiskLevel;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdatePortfolioRequest {
    @NotBlank
    private String name;

    @NotNull
    private RiskLevel riskLevel;
}
