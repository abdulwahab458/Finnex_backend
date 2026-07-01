package com.finnex.finance_app.domain.portfolio.dto.reponse;

import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
public class HoldingResponse {
    private UUID id;
    private String symbol;
    private String companyName;
    private String sector;

    private int quantity;

    private BigDecimal averageCostBasis;

    private BigDecimal currentPrice;

    private BigDecimal previousClose;

    private BigDecimal dayChangePercent;

    private BigDecimal currentValue;

    private BigDecimal totalReturnPercent;

}
