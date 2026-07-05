package com.finnex.finance_app.domain.portfolio.service;

import com.finnex.finance_app.common.enums.PortfolioPerformancePeriod;
import com.finnex.finance_app.domain.portfolio.dto.reponse.HoldingResponse;
import com.finnex.finance_app.domain.portfolio.dto.reponse.PortfolioAllocationResponse;
import com.finnex.finance_app.domain.portfolio.dto.reponse.PortfolioPerformanceResponse;
import com.finnex.finance_app.domain.portfolio.dto.reponse.PortfolioResponse;
import com.finnex.finance_app.domain.portfolio.dto.request.CreateHoldingRequest;
import com.finnex.finance_app.domain.portfolio.dto.request.CreatePortfolioRequest;
import com.finnex.finance_app.domain.portfolio.dto.request.UpdateHoldingRequest;
import com.finnex.finance_app.domain.portfolio.dto.request.UpdatePortfolioRequest;
import com.finnex.finance_app.domain.user.entity.User;

import java.util.List;
import java.util.UUID;

public interface PortfolioService {
    PortfolioResponse createPortfolio(User currentUser, CreatePortfolioRequest request);

    List<PortfolioResponse> getPortfolios(User currenUser);

    PortfolioResponse getPortfolioById(User currentUser, UUID portfolioId);

    PortfolioResponse updatePortfolio(User currentUser, UUID portfolioId, UpdatePortfolioRequest request);

    void deletePortfolio(User currentUser, UUID portfolioId);

    HoldingResponse createHolding(User currentUser, UUID portfolioId, CreateHoldingRequest request);

    List<HoldingResponse> getHoldings(User currentUser, UUID portfolioId);

    HoldingResponse updateHolding(User currentUser, UUID portfolioId, UUID holdingId, UpdateHoldingRequest request);

    void deleteHolding(User currentUser, UUID portfolioId, UUID holdingId);

    List<PortfolioAllocationResponse> getPortfolioAllocation(User currentUser, UUID portfolioId);

    PortfolioPerformanceResponse getPortfolioPeformance(User currentUser, UUID portfolioId, PortfolioPerformancePeriod period);



}
