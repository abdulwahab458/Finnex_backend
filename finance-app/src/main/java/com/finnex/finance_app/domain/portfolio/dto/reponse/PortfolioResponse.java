package com.finnex.finance_app.domain.portfolio.dto.reponse;

import com.finnex.finance_app.common.enums.RiskLevel;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
public class PortfolioResponse {
    private UUID id;
    private  String name;
    private RiskLevel riskLevel;
    private BigDecimal totalInvested;
    private BigDecimal currentValue;
    private BigDecimal totalReturnPercent;


}
