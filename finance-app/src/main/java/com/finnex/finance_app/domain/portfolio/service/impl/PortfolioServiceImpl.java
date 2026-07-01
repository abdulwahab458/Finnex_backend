package com.finnex.finance_app.domain.portfolio.service.impl;

import com.finnex.finance_app.domain.portfolio.dto.reponse.PortfolioResponse;
import com.finnex.finance_app.domain.portfolio.dto.request.CreatePortfolioRequest;
import com.finnex.finance_app.domain.portfolio.entity.Portfolio;
import com.finnex.finance_app.domain.portfolio.mapper.PortfolioMapper;
import com.finnex.finance_app.domain.portfolio.repository.PortfolioRepository;
import com.finnex.finance_app.domain.portfolio.service.PortfolioService;
import com.finnex.finance_app.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class PortfolioServiceImpl implements PortfolioService {
    private final PortfolioRepository portfolioRepository;
    private final PortfolioMapper portfolioMapper;
    @Override
    @Transactional
    public PortfolioResponse createPortfolio(User currentUser, CreatePortfolioRequest request) {
        Portfolio portfolio = portfolioMapper.toEntity(request);
        portfolio.setUser(currentUser);
        portfolioRepository.save(portfolio);
        return portfolioMapper.toResponse(portfolio);
    }
}
