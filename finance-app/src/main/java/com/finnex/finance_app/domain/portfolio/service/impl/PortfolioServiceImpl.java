package com.finnex.finance_app.domain.portfolio.service.impl;

import com.finnex.finance_app.common.enums.PortfolioPerformancePeriod;
import com.finnex.finance_app.common.exceptions.BadRequestException;
import com.finnex.finance_app.common.exceptions.DuplicateResourceException;
import com.finnex.finance_app.common.exceptions.ResourceNotFound;
import com.finnex.finance_app.domain.portfolio.dto.reponse.*;
import com.finnex.finance_app.domain.portfolio.dto.request.CreateHoldingRequest;
import com.finnex.finance_app.domain.portfolio.dto.request.CreatePortfolioRequest;
import com.finnex.finance_app.domain.portfolio.dto.request.UpdateHoldingRequest;
import com.finnex.finance_app.domain.portfolio.dto.request.UpdatePortfolioRequest;
import com.finnex.finance_app.domain.portfolio.entity.Portfolio;
import com.finnex.finance_app.domain.portfolio.entity.Stock;
import com.finnex.finance_app.domain.portfolio.entity.StockHolding;
import com.finnex.finance_app.domain.portfolio.mapper.HoldingMapper;
import com.finnex.finance_app.domain.portfolio.mapper.PortfolioMapper;
import com.finnex.finance_app.domain.portfolio.repository.PortfolioRepository;
import com.finnex.finance_app.domain.portfolio.repository.StockHoldingRepository;
import com.finnex.finance_app.domain.portfolio.repository.StockRepository;
import com.finnex.finance_app.domain.portfolio.service.PortfolioService;
import com.finnex.finance_app.domain.portfolio.stock.dto.StockCandelResponse;
import com.finnex.finance_app.domain.portfolio.stock.service.StockApiService;
import com.finnex.finance_app.domain.portfolio.util.PerformanceDateRange;
import com.finnex.finance_app.domain.portfolio.util.PortfolioPerformanceUtil;
import com.finnex.finance_app.domain.user.entity.User;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PortfolioServiceImpl implements PortfolioService {
    private final PortfolioRepository portfolioRepository;
    private final StockHoldingRepository  stockHoldingRepository;
    private final StockRepository stockRepository;
    private final PortfolioMapper portfolioMapper;
    private  final StockApiService  stockApiService;
    private final HoldingMapper holdingMapper;
    @Override
    @Transactional
    public PortfolioResponse createPortfolio(User currentUser, CreatePortfolioRequest request) {
        if(portfolioRepository.findByUser(currentUser)
                .stream()
                .anyMatch(p->p.getName().equalsIgnoreCase(request.getName()))){
            throw  new DuplicateResourceException(
                    "Portfolio already exists");
        }
        Portfolio portfolio = portfolioMapper.toEntity(request);
        portfolio.setUser(currentUser);
        portfolioRepository.save(portfolio);
        return portfolioMapper.toResponse(portfolio);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PortfolioResponse> getPortfolios(User currenUser) {
        List<Portfolio> portfolios = portfolioRepository.findByUser(currenUser);
        return portfolios.stream().map(portfolioMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public PortfolioResponse getPortfolioById(
            User currentUser,
            UUID portfolioId
    ) {

        Portfolio portfolio = portfolioRepository
                .findByIdAndUser(
                        portfolioId,
                        currentUser
                )
                .orElseThrow(() ->
                        new ResourceNotFound(
                                "Portfolio not found."
                        )
                );

        return portfolioMapper.toResponse(portfolio);
    }

    @Override
    @Transactional
    public PortfolioResponse updatePortfolio(User currentUser, UUID portfolioId, UpdatePortfolioRequest request) {
        if(portfolioRepository.findByUser(currentUser)
                .stream()
                .anyMatch(p->p.getName().equalsIgnoreCase(request.getName()))){
            throw  new DuplicateResourceException(
                    "Portfolio already exists");
        }
        Portfolio portfolio = portfolioRepository.findByIdAndUser(portfolioId,currentUser).orElseThrow(
                () -> new ResourceNotFound("Portfolio not found.")
        );

        portfolio.setName(request.getName());
        portfolio.setRiskLevel(request.getRiskLevel());
        Portfolio updatedPortfolio = portfolioRepository.save(portfolio);
        return portfolioMapper.toResponse(updatedPortfolio);
    }

    @Override
    @Transactional
    public void deletePortfolio(User currentUser, UUID portfolioId) {
        Portfolio portfolio = portfolioRepository.findByIdAndUser(portfolioId,currentUser).orElseThrow(
                () -> new ResourceNotFound("Portfolio not found.")
        );
        if(stockHoldingRepository.existsByPortfolio(portfolio)){
            throw  new BadRequestException(
                    "Portfolio contains holdings. Remove them before deleting the portfolio."
            );
        }

        portfolioRepository.delete(portfolio);

    }

    @Override
    @Transactional
    public HoldingResponse createHolding(User currentUser, UUID portfolioId, CreateHoldingRequest request) {
        Portfolio portfolio = portfolioRepository.findByIdAndUser(portfolioId,currentUser).orElseThrow(
                ()->
                        new ResourceNotFound("Portfolio not found.")
        );
        Stock stock = stockRepository.findBySymbol(request.getSymbol()).orElse(null);
        if(stock == null){
            stock = stockApiService.fetchAndSaveStock(request.getSymbol());
        }else {
            stock = stockApiService.refreshStock(stock);
        }

        Optional<StockHolding> existingHolding = stockHoldingRepository.findByPortfolioAndStock(portfolio,stock);
        StockHolding holding;
        if(existingHolding.isPresent()){
             holding = existingHolding.get();
            int oldQuantity = holding.getQuantity();
            int newQuantity = request.getQuantity();
            BigDecimal oldCost = holding.getAverageCostBasis();
            BigDecimal newCost = request.getAverageCostBasis();

            BigDecimal totalCost =
                    oldCost.multiply(BigDecimal.valueOf(oldQuantity))
                            .add(
                                    newCost.multiply(BigDecimal.valueOf(newQuantity))
                            );

            int totalQuantity = oldQuantity + newQuantity;
            BigDecimal averageCostBasis =
                    totalCost.divide(
                            BigDecimal.valueOf(totalQuantity),
                            4,
                            RoundingMode.HALF_UP
                    );

            holding.setQuantity(totalQuantity);
            holding.setAverageCostBasis(averageCostBasis);

            BigDecimal currentValue =
                    stock.getCurrentPrice()
                            .multiply(
                                    BigDecimal.valueOf(totalQuantity)
                            );

            holding.setCurrentValue(currentValue);

            BigDecimal invested =
                    averageCostBasis
                            .multiply(BigDecimal.valueOf(totalQuantity));

            holding = calculateAndSaveHolding(stock, holding, currentValue, invested);
        }else {

            holding = holdingMapper.toEntity(request);
            holding.setPortfolio(portfolio);
            holding.setStock(stock);
            BigDecimal currentValue =
                    stock.getCurrentPrice()
                            .multiply(
                                    BigDecimal.valueOf(
                                            request.getQuantity()
                                    )
                            );
            holding.setCurrentValue(currentValue);

            BigDecimal invested =
                    request.getAverageCostBasis()
                            .multiply(
                                    BigDecimal.valueOf(
                                            request.getQuantity()
                                    )
                            );
            holding = calculateAndSaveHolding(stock, holding, currentValue, invested);
        }
        updatePortfolioSummary(portfolio);
        return  holdingMapper.toResponse(holding);

    }

    @Override
    @Transactional(readOnly = true)
    public List<HoldingResponse> getHoldings(User currentUser, UUID portfolioId) {
        Portfolio portfolio = portfolioRepository.findByIdAndUser(portfolioId,currentUser).orElseThrow(
                () -> new ResourceNotFound("Portfolio not found.")
        );

        List<StockHolding> holdings = stockHoldingRepository.findByPortfolio(portfolio);
        holdings.forEach(holding ->
                holding.setStock(
                        stockApiService.refreshStock(
                                holding.getStock()
                        )
                )
        );
        return holdings.stream()
                .map(
                        holdingMapper::toResponse)
                .toList();

    }

    @Override
    @Transactional
    public HoldingResponse updateHolding(User currentUser, UUID portfolioId, UUID holdingId, UpdateHoldingRequest request) {
        Portfolio portfolio = portfolioRepository.findByIdAndUser(portfolioId,currentUser).orElseThrow(
                () -> new ResourceNotFound("Portfolio not found.")
        );
        StockHolding holding = stockHoldingRepository.findByIdAndPortfolio(holdingId,portfolio)
                .orElseThrow(() -> new ResourceNotFound("Holding not found."));

        Stock stock = stockApiService.refreshStock(holding.getStock());
        holding.setQuantity(request.getQuantity());
        holding.setAverageCostBasis(request.getAverageCostBasis());
        BigDecimal currentValue =
                stock.getCurrentPrice()
                        .multiply(
                                BigDecimal.valueOf(request.getQuantity())
                        );

        holding.setCurrentValue(currentValue);

        BigDecimal invested =
                request.getAverageCostBasis()
                        .multiply(
                                BigDecimal.valueOf(request.getQuantity())
                        );

        holding = calculateAndSaveHolding(stock, holding, currentValue, invested);
        updatePortfolioSummary(portfolio);
        return holdingMapper.toResponse(holding);
    }

    @Override
    public void deleteHolding(User currentUser, UUID portfolioId, UUID holdingId) {
        Portfolio portfolio = portfolioRepository.findByIdAndUser(portfolioId,currentUser)
                .orElseThrow(
                () -> new ResourceNotFound("Portfolio not found.")
        );

        StockHolding holding = stockHoldingRepository.findByIdAndPortfolio(holdingId,portfolio)
                .orElseThrow(() -> new ResourceNotFound("Holding not found."));

        stockHoldingRepository.delete(holding);
        updatePortfolioSummary(portfolio);
    }

    @Override
    public List<PortfolioAllocationResponse> getPortfolioAllocation(User currentUser, UUID portfolioId) {
        Portfolio portfolio = portfolioRepository.findByIdAndUser(portfolioId,currentUser)
                .orElseThrow(
                        () -> new ResourceNotFound("Portfolio not found.")
                );
        List<StockHolding> holdings = stockHoldingRepository.findByPortfolio(portfolio);

        if(holdings.isEmpty()){
            return List.of();
        }

        holdings.forEach(holding ->
                holding.setStock(
                        stockApiService.refreshStock(
                                holding.getStock()
                        )
                )
        );

        Map<String, BigDecimal> sectorAllocation =
                holdings.stream()
                        .collect(
                                Collectors.groupingBy(
                                        holding -> holding.getStock().getSector(),
                                        Collectors.reducing(
                                                BigDecimal.ZERO,
                                                StockHolding::getCurrentValue,
                                                BigDecimal::add
                                        )
                                )
                        );

        BigDecimal portfolioValue = portfolio.getCurrentValue();
        return sectorAllocation.entrySet()
                .stream()
                .map(entry -> {

                    PortfolioAllocationResponse response =
                            new PortfolioAllocationResponse();

                    response.setSector(
                            entry.getKey()
                    );

                    response.setCurrentValue(
                            entry.getValue()
                    );

                    BigDecimal allocationPercentage;

                    if (portfolioValue.compareTo(BigDecimal.ZERO) == 0) {

                        allocationPercentage = BigDecimal.ZERO;

                    } else {

                        allocationPercentage =
                                entry.getValue()
                                        .divide(
                                                portfolioValue,
                                                4,
                                                RoundingMode.HALF_UP
                                        )
                                        .multiply(
                                                BigDecimal.valueOf(100)
                                        );
                    }

                    response.setPercentage(
                            allocationPercentage
                    );

                    return response;

                })
                .toList();

    }

    @Override
    @Transactional(readOnly = true)
    public PortfolioPerformanceResponse getPortfolioPeformance(User currentUser, UUID portfolioId, PortfolioPerformancePeriod period) {
        Portfolio portfolio = portfolioRepository.findByIdAndUser(portfolioId,currentUser).orElseThrow(
                () -> new ResourceNotFound("Portfolio not found.")
        );
        List<StockHolding> holdings =
                stockHoldingRepository.findByPortfolio(portfolio);

        if (holdings.isEmpty()) {
            PortfolioPerformanceResponse response =
                    new PortfolioPerformanceResponse();

            response.setTimeline(List.of());
            response.setMinPortfolioValue(BigDecimal.ZERO);
            response.setMaxPortfolioValue(BigDecimal.ZERO);

            return response;
        }
        PerformanceDateRange range =
                PortfolioPerformanceUtil.getDateRange(period);

        Map<LocalDate, BigDecimal> portfolioTimeline =
                new TreeMap<>();

        for (StockHolding holding : holdings) {

            StockCandelResponse candles =
                    stockApiService.getHistoricalCandles(
                            holding.getStock().getSymbol(),
                            range.from(),
                            range.to(),
                            range.fullHistory()
                    );

            if (candles == null
                    || !"ok".equalsIgnoreCase(candles.getStatus())
                    || candles.getClosePrices() == null
                    || candles.getTimestamps() == null) {
                continue;
            }

            List<BigDecimal> closePrices = candles.getClosePrices();
            List<Long> timestamps = candles.getTimestamps();

            for (int i = 0; i < timestamps.size(); i++) {

                LocalDate date =
                        Instant.ofEpochSecond(
                                        timestamps.get(i)
                                )
                                .atZone(ZoneOffset.UTC)
                                .toLocalDate();

                BigDecimal holdingValue =
                        closePrices.get(i)
                                .multiply(
                                        BigDecimal.valueOf(
                                                holding.getQuantity()
                                        )
                                );

                portfolioTimeline.merge(
                        date,
                        holdingValue,
                        BigDecimal::add
                );
            }
        }

        List<PortfolioPerformancePoint> timeline =
                portfolioTimeline.entrySet()
                        .stream()
                        .map(entry ->
                                new PortfolioPerformancePoint(
                                        entry.getKey(),
                                        entry.getValue()
                                )
                        )
                        .toList();

        BigDecimal minPortfolioValue =
                portfolioTimeline.values()
                        .stream()
                        .min(BigDecimal::compareTo)
                        .orElse(BigDecimal.ZERO);

        BigDecimal maxPortfolioValue =
                portfolioTimeline.values()
                        .stream()
                        .max(BigDecimal::compareTo)
                        .orElse(BigDecimal.ZERO);

        PortfolioPerformanceResponse response =
                new PortfolioPerformanceResponse();

        response.setTimeline(timeline);
        response.setMinPortfolioValue(minPortfolioValue);
        response.setMaxPortfolioValue(maxPortfolioValue);

        return response;

    }

    @NonNull
    private StockHolding calculateAndSaveHolding(Stock stock, StockHolding holding, BigDecimal currentValue, BigDecimal invested) {
        BigDecimal totalReturnPercent;
        if(invested.compareTo(BigDecimal.ZERO)==0){
            totalReturnPercent = BigDecimal.ZERO;
        }else{

        totalReturnPercent =
                currentValue
                        .subtract(invested)
                        .divide(
                                invested,
                                4,
                                RoundingMode.HALF_UP
                        )
                        .multiply(
                                BigDecimal.valueOf(100)
                        );
        }

        holding.setTotalReturnPercent(totalReturnPercent);

        holding.setDayChangePercent(
                stock.getDayChangePercent()
        );
        holding = stockHoldingRepository.save(holding);
        return holding;
    }

    private void updatePortfolioSummary(
            Portfolio portfolio
    ) {

        List<StockHolding> holdings =
                stockHoldingRepository.findByPortfolio(portfolio);

        BigDecimal totalInvested = holdings.stream()
                .map(holding ->
                        holding.getAverageCostBasis()
                                .multiply(
                                        BigDecimal.valueOf(
                                                holding.getQuantity()
                                        )
                                )
                )
                .reduce(
                        BigDecimal.ZERO,
                        BigDecimal::add
                );

        BigDecimal currentValue = holdings.stream()
                .map(StockHolding::getCurrentValue)
                .reduce(
                        BigDecimal.ZERO,
                        BigDecimal::add
                );

        BigDecimal totalReturnPercent;

        if (totalInvested.compareTo(BigDecimal.ZERO) == 0) {
            totalReturnPercent = BigDecimal.ZERO;
        } else {
            totalReturnPercent = currentValue
                    .subtract(totalInvested)
                    .divide(
                            totalInvested,
                            4,
                            RoundingMode.HALF_UP
                    )
                    .multiply(
                            BigDecimal.valueOf(100)
                    );
        }

        portfolio.setTotalInvested(totalInvested);
        portfolio.setCurrentValue(currentValue);
        portfolio.setTotalReturnPercent(totalReturnPercent);

        portfolioRepository.save(portfolio);
    }
}
