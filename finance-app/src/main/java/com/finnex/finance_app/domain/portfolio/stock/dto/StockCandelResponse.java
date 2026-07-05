package com.finnex.finance_app.domain.portfolio.stock.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class StockCandelResponse {
    @JsonProperty("c")
    private List<BigDecimal> closePrices;

    @JsonProperty("h")
    private List<BigDecimal> highPrices;

    @JsonProperty("l")
    private List<BigDecimal> lowPrices;

    @JsonProperty("o")
    private List<BigDecimal> openPrices;

    @JsonProperty("t")
    private List<Long> timestamps;

    @JsonProperty("v")
    private List<Long> volumes;

    @JsonProperty("s")
    private String status;
}
