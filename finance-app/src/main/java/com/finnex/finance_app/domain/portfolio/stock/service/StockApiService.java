package com.finnex.finance_app.domain.portfolio.stock.service;

import com.finnex.finance_app.domain.portfolio.entity.Stock;

public interface StockApiService {
    Stock fetchAndSaveStock(String symbol);
    Stock refreshStock(Stock stock);
}
