package com.finnex.finance_app.domain.portfolio.stock.service;

import com.finnex.finance_app.domain.portfolio.entity.Stock;
import com.finnex.finance_app.domain.portfolio.stock.dto.SearchStockItem;
import com.finnex.finance_app.domain.portfolio.stock.dto.StockCandelResponse;

import java.util.List;

public interface StockApiService {
    Stock fetchAndSaveStock(String symbol);
    Stock refreshStock(Stock stock);
    StockCandelResponse getHistoricalCandles(String symbol,long from,long to,boolean fullHistory);
    List<SearchStockItem> searchStock(String query);

}
