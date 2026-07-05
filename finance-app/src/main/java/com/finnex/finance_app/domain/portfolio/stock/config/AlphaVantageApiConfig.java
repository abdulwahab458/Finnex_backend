package com.finnex.finance_app.domain.portfolio.stock.config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
@RequiredArgsConstructor
@EnableConfigurationProperties(AlphaVantageApiProperties.class)
public class AlphaVantageApiConfig {

    private final AlphaVantageApiProperties properties;

    @Bean
    public RestClient alphaVantageRestClient() {

        return RestClient.builder()
                .baseUrl(properties.getBaseUrl())
                .build();
    }
}