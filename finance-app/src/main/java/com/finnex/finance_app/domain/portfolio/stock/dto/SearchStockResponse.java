package com.finnex.finance_app.domain.portfolio.stock.dto;

import lombok.Data;

import java.util.List;

@Data
public class SearchStockResponse {

    private int count;

    private List<SearchStockItem> result;

}
