package com.finnex.finance_app.domain.portfolio.stock.dto;

import lombok.Data;

@Data
public class SearchStockItem {
    private String description;

    private String displaySymbol;

    private String symbol;

    private String type;
}
