package com.finnex.finance_app.domain.portfolio.repository;

import com.finnex.finance_app.domain.portfolio.entity.Stock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface StockRepository extends JpaRepository<Stock, UUID> {
    Optional<Stock> findBySymbol(String symbol);
    Boolean existsBySymbol(String symbol);
}
