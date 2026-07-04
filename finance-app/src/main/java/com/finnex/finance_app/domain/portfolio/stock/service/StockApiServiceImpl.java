package com.finnex.finance_app.domain.portfolio.stock.service;

import com.finnex.finance_app.common.exceptions.ExternalServiceException;
import com.finnex.finance_app.domain.portfolio.entity.Stock;
import com.finnex.finance_app.domain.portfolio.repository.StockRepository;
import com.finnex.finance_app.domain.portfolio.stock.config.StockApiProperties;
import com.finnex.finance_app.domain.portfolio.stock.dto.CompanyProfileResponse;
import com.finnex.finance_app.domain.portfolio.stock.dto.StockQuoteReponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
@RequiredArgsConstructor
public class StockApiServiceImpl implements StockApiService{
    private final StockRepository stockRepository;
    private final StockApiProperties stockApiProperties;
    private final RestClient restClient;

    @Override
    public Stock fetchAndSaveStock(String symbol) {

        CompanyProfileResponse profile;

        try {

         profile =
        restClient.get()
                .uri("/stock/profile2?symbol={symbol}&token={token}",
                        symbol,
                        stockApiProperties.getKey())
                .retrieve()
                .body(CompanyProfileResponse.class);
        }catch (Exception e){
            throw new ExternalServiceException(
                    "Unable to fetch company profile for symbol: " + symbol
                    + "error :" + e.getMessage()
            );
        }

        if (profile == null || profile.getName() == null) {
            throw new ExternalServiceException(
                    "Invalid stock symbol: " + symbol
            );
        }

        StockQuoteReponse quote;
        try
        {

               quote =
                restClient.get()
                        .uri(
                                 "/quote?symbol={symbol}&token={token}",
                                symbol,
                                stockApiProperties.getKey()
                        )
                        .retrieve()
                        .body(StockQuoteReponse.class);
        } catch (Exception e){
            throw new ExternalServiceException(
                    "Unable to fetch quote for symbol: " + symbol
                            + "error :" + e.getMessage()
            );
        }

        if (quote == null || quote.getCurrentPrice() == null) {
            throw new ExternalServiceException(
                    "Unable to retrieve latest stock price."
            );
        }


        Stock stock = new Stock();
        stock.setSymbol(profile.getTicker());
        stock.setCompanyName(profile.getName());
        stock.setSector(profile.getFinnhubIndustry());
        stock.setMarketCap(profile.getMarketCapitalization());
        stock.setCurrentPrice(
                quote.getCurrentPrice()
        );
        stock.setPreviousClose(
                quote.getPreviousClose()
        );
        stock.setDayChangePercent(
                calculateDayChangePercent(
                        quote.getCurrentPrice(),
                        quote.getPreviousClose())
        );

        return stockRepository.save(stock);

    }

    private BigDecimal calculateDayChangePercent(
            BigDecimal currentPrice,
            BigDecimal previousClose
    ) {
        if (previousClose == null
                || previousClose.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }

        return currentPrice
                .subtract(previousClose)
                .divide(previousClose, 4, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100))
                .setScale(2, RoundingMode.HALF_UP);
    }

    @Override
    public Stock refreshStock(Stock stock) {

        StockQuoteReponse quote;
        try
        {

                quote =
                restClient.get()
                        .uri(
                                stockApiProperties.getBaseUrl()
                                        + "/quote?symbol={symbol}&token={token}",
                                stock.getSymbol(),
                                stockApiProperties.getKey()
                        )
                        .retrieve()
                        .body(StockQuoteReponse.class);
        }catch (Exception e){
            throw new ExternalServiceException(
                    "Unable to fetch quote for symbol: " + stock.getSymbol()
                    + "error :" + e.getMessage()
            );
        }

        if (quote == null || quote.getCurrentPrice() == null) {
            throw new ExternalServiceException(
                    "Unable to retrieve latest stock price."
            );
        }

        stock.setCurrentPrice(
                quote.getCurrentPrice()
        );

        stock.setPreviousClose(
                quote.getPreviousClose()
        );

        stock.setDayChangePercent(
                calculateDayChangePercent(
                        quote.getCurrentPrice(),
                        quote.getPreviousClose()
                )
        );

        return stockRepository.save(stock);
    }
}
