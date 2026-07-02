package com.finnex.finance_app.domain.portfolio.service;

import com.finnex.finance_app.domain.portfolio.dto.reponse.PortfolioResponse;
import com.finnex.finance_app.domain.portfolio.dto.request.CreatePortfolioRequest;
import com.finnex.finance_app.domain.portfolio.dto.request.UpdatePortfolioRequest;
import com.finnex.finance_app.domain.portfolio.entity.Portfolio;
import com.finnex.finance_app.domain.user.entity.User;

import java.util.List;
import java.util.UUID;

public interface PortfolioService {
    PortfolioResponse createPortfolio(User currentUser, CreatePortfolioRequest request);
    List<PortfolioResponse> getPortfolios(User currenUser);
    PortfolioResponse getPortfolioById(User currentUser, UUID portfolioId);
    PortfolioResponse updatePortfolio(User currentUser, UUID portfolioId, UpdatePortfolioRequest request);
    void deletePortfolio(User currentUser, UUID portfolioId);

}
