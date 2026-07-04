package com.finnex.finance_app.domain.portfolio.stock.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class StockQuoteReponse {
    @JsonProperty("c")
    private BigDecimal currentPrice;

    @JsonProperty("d")
    private BigDecimal change;

    @JsonProperty("dp")
    private BigDecimal changePercent;

    @JsonProperty("h")
    private BigDecimal highPrice;

    @JsonProperty("l")
    private BigDecimal lowPrice;

    @JsonProperty("o")
    private BigDecimal openPrice;

    @JsonProperty("pc")
    private BigDecimal previousClose;

    @JsonProperty("t")
    private Long timestamp;
}



