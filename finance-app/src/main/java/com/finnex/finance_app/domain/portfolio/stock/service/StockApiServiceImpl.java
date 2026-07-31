package com.finnex.finance_app.domain.portfolio.stock.service;

import com.finnex.finance_app.common.exceptions.ExternalServiceException;
import com.finnex.finance_app.domain.portfolio.entity.Stock;
import com.finnex.finance_app.domain.portfolio.repository.StockRepository;
import com.finnex.finance_app.domain.portfolio.stock.config.AlphaVantageApiProperties;
import com.finnex.finance_app.domain.portfolio.stock.config.FinnhubApiProperties;
import com.finnex.finance_app.domain.portfolio.stock.dto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
@Service
public class StockApiServiceImpl implements StockApiService {

    private final StockRepository stockRepository;
    private final FinnhubApiProperties finnhubApiProperties;
    private final RestClient restClient;
    private final AlphaVantageApiProperties alphaVantageApiProperties;
    private final RestClient alphaVantageRestClient;

    public StockApiServiceImpl(
            StockRepository stockRepository,
            FinnhubApiProperties finnhubApiProperties,
            @Qualifier("finnhubApiRestClient") RestClient restClient,
            AlphaVantageApiProperties alphaVantageApiProperties,
            @Qualifier("alphaVantageRestClient") RestClient alphaVantageRestClient
    ) {
        this.stockRepository = stockRepository;
        this.finnhubApiProperties = finnhubApiProperties;
        this.restClient = restClient;
        this.alphaVantageApiProperties = alphaVantageApiProperties;
        this.alphaVantageRestClient = alphaVantageRestClient;
    }

    @Override
    public Stock fetchAndSaveStock(String symbol) {

        CompanyProfileResponse profile;

        try {

            profile =
                    restClient.get()
                            .uri("/stock/profile2?symbol={symbol}&token={token}",
                                    symbol,
                                    finnhubApiProperties.getKey())
                            .retrieve()
                            .body(CompanyProfileResponse.class);
        } catch (Exception e) {
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
        try {

            quote =
                    restClient.get()
                            .uri(
                                    "/quote?symbol={symbol}&token={token}",
                                    symbol,
                                    finnhubApiProperties.getKey()
                            )
                            .retrieve()
                            .body(StockQuoteReponse.class);
        } catch (Exception e) {
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
        try {

            quote =
                    restClient.get()
                            .uri(
                                    finnhubApiProperties.getBaseUrl()
                                            + "/quote?symbol={symbol}&token={token}",
                                    stock.getSymbol(),
                                    finnhubApiProperties.getKey()
                            )
                            .retrieve()
                            .body(StockQuoteReponse.class);
        } catch (Exception e) {
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

    @Override
    public StockCandelResponse getHistoricalCandles(String symbol, long from, long to, boolean fullHistory) {
        AlphaVantageResponse response;
        String outputSize = fullHistory ? "full" : "compact";
        try {

            response =
                    alphaVantageRestClient.get()
                            .uri(
                                    "/query?function=TIME_SERIES_DAILY&symbol={symbol}&outputsize={outputSize}&apikey={apikey}",
                                    symbol,
                                    outputSize,
                                    alphaVantageApiProperties.getKey()
                            )
                            .retrieve()
                            .body(AlphaVantageResponse.class);


        } catch (Exception e) {
            System.out.println(e.getMessage());
            throw new ExternalServiceException(
                    "Unable to fetch historical candles for symbol: "
                            + symbol
                            + ". Error: "
                            + e.getMessage()
            );

        }

        if (response == null
                || response.getTimeSeries() == null
                || response.getTimeSeries().isEmpty()) {

            throw new ExternalServiceException(
                    "Unable to retrieve historical stock data for symbol: " + symbol
            );
        }

        StockCandelResponse candleResponse =
                new StockCandelResponse();

        LocalDate fromDate =
                Instant.ofEpochSecond(from)
                        .atZone(ZoneOffset.UTC)
                        .toLocalDate();

        LocalDate toDate =
                Instant.ofEpochSecond(to)
                        .atZone(ZoneOffset.UTC)
                        .toLocalDate();

        List<BigDecimal> closePrices = new ArrayList<>();
        List<Long> timestamps = new ArrayList<>();
        List<Long> volumes = new ArrayList<>();
        response.getTimeSeries()
                .entrySet()
                .stream()
                .sorted(Map.Entry.comparingByKey())
                .forEach(entry -> {

                    LocalDate date = LocalDate.parse(entry.getKey());

                    if (date.isBefore(fromDate) || date.isAfter(toDate)) {
                        return;
                    }

                    AlphaVantageDailyData data =
                            entry.getValue();

                    timestamps.add(
                            date.atStartOfDay(ZoneOffset.UTC)
                                    .toEpochSecond()
                    );

                    closePrices.add(
                            data.getClose()
                    );

                    volumes.add(
                            data.getVolume()
                    );

                });

        candleResponse.setClosePrices(closePrices);
        candleResponse.setTimestamps(timestamps);
        candleResponse.setVolumes(volumes);
        candleResponse.setStatus("ok");

        return candleResponse;
    }

    @Override
    public List<SearchStockItem> searchStock(String query) {
        SearchStockResponse searchStockResponse;
        try{
            searchStockResponse =
                    restClient.get()
                            .uri(
                                    "/search?q={query}&token={token}",
                                    query,
                                    finnhubApiProperties.getKey()
                            )
                            .retrieve()
                            .body(SearchStockResponse.class);

        }catch(Exception e){
            throw new ExternalServiceException(
                    "Unable to fetch query: " + query
                            + "error :" + e.getMessage()
            );
        }

        if (searchStockResponse == null
                || searchStockResponse.getResult() == null) {

            throw new ExternalServiceException(
                    "Unable to search stocks for query: " + query
            );
        }
        return searchStockResponse.getResult();
    }
}
