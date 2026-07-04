package com.finnex.finance_app.domain.portfolio.repository;

import com.finnex.finance_app.domain.portfolio.entity.Portfolio;
import com.finnex.finance_app.domain.portfolio.entity.Stock;
import com.finnex.finance_app.domain.portfolio.entity.StockHolding;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface StockHoldingRepository extends JpaRepository<StockHolding, UUID> {
    List<StockHolding> findByPortfolio(
            Portfolio portfolio
    );

    Optional<StockHolding> findByIdAndPortfolio(
            UUID id,
            Portfolio portfolio
    );

    boolean existsByPortfolio(Portfolio portfolio);

    Optional<StockHolding> findByPortfolioAndStock(
            Portfolio portfolio,
            Stock stock
    );
}
