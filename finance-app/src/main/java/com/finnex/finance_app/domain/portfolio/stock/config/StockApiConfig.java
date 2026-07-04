package com.finnex.finance_app.domain.portfolio.stock.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
@RequiredArgsConstructor
public class StockApiConfig {
    private final StockApiProperties stockApiProperties;
    @Bean
    public RestClient stockApiRestClient(RestClient.Builder builder) {
        return builder.baseUrl(stockApiProperties.getBaseUrl()).build();
    }
}
