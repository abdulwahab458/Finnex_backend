package com.finnex.finance_app.domain.portfolio.stock.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "alphavantagestock.alpha-vantage")
public class AlphaVantageApiProperties {

    private String baseUrl;

    private String key;

}