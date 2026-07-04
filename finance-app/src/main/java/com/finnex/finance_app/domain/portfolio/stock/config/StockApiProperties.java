package com.finnex.finance_app.domain.portfolio.stock.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "stock.api")
public class StockApiProperties {
    private String key;
    private String baseUrl;
}
