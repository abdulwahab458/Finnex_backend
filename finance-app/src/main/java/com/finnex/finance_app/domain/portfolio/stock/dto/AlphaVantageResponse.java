package com.finnex.finance_app.domain.portfolio.stock.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.Map;

@Data
public class AlphaVantageResponse {

    @JsonProperty("Time Series (Daily)")
    private Map<String, AlphaVantageDailyData> timeSeries;

}