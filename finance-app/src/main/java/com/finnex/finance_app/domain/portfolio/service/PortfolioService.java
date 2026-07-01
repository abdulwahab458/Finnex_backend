package com.finnex.finance_app.domain.portfolio.service;

import com.finnex.finance_app.domain.portfolio.dto.reponse.PortfolioResponse;
import com.finnex.finance_app.domain.portfolio.dto.request.CreatePortfolioRequest;
import com.finnex.finance_app.domain.portfolio.entity.Portfolio;
import com.finnex.finance_app.domain.user.entity.User;

public interface PortfolioService {
    PortfolioResponse createPortfolio(User currentUser, CreatePortfolioRequest request);
}
