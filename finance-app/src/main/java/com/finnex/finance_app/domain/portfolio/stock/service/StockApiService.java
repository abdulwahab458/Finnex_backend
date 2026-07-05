package com.finnex.finance_app.domain.portfolio.stock.service;

import com.finnex.finance_app.domain.portfolio.entity.Stock;
import com.finnex.finance_app.domain.portfolio.stock.dto.StockCandelResponse;

public interface StockApiService {
    Stock fetchAndSaveStock(String symbol);
    Stock refreshStock(Stock stock);
    StockCandelResponse getHistoricalCandles(String symbol,long from,long to,boolean fullHistory);

}
