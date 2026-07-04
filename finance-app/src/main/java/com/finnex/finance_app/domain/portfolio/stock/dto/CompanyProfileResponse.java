package com.finnex.finance_app.domain.portfolio.stock.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class CompanyProfileResponse {
    private String ticker;

    private String name;

    private String finnhubIndustry;

    private BigDecimal marketCapitalization;

    private String exchange;

    private String currency;

    private String country;

    private String logo;
}
