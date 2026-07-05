package com.finnex.finance_app.domain.portfolio.stock.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
@RequiredArgsConstructor
public class FinnhubApiConfig {
    private final FinnhubApiProperties finnhubApiProperties;
    @Bean
    public RestClient finnhubApiRestClient(RestClient.Builder builder) {
        return builder.baseUrl(finnhubApiProperties.getBaseUrl()).build();
    }
}
