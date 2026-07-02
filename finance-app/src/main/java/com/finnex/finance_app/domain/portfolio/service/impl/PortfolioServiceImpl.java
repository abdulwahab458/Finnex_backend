package com.finnex.finance_app.domain.portfolio.service.impl;

import com.finnex.finance_app.common.exceptions.DuplicateResourceException;
import com.finnex.finance_app.common.exceptions.ResourceNotFound;
import com.finnex.finance_app.domain.portfolio.dto.reponse.PortfolioResponse;
import com.finnex.finance_app.domain.portfolio.dto.request.CreatePortfolioRequest;
import com.finnex.finance_app.domain.portfolio.dto.request.UpdatePortfolioRequest;
import com.finnex.finance_app.domain.portfolio.entity.Portfolio;
import com.finnex.finance_app.domain.portfolio.mapper.PortfolioMapper;
import com.finnex.finance_app.domain.portfolio.repository.PortfolioRepository;
import com.finnex.finance_app.domain.portfolio.service.PortfolioService;
import com.finnex.finance_app.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PortfolioServiceImpl implements PortfolioService {
    private final PortfolioRepository portfolioRepository;
    private final PortfolioMapper portfolioMapper;
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

        portfolioRepository.delete(portfolio);

    }
}
