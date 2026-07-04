package com.finnex.finance_app.domain.portfolio.repository;

import com.finnex.finance_app.domain.portfolio.dto.reponse.PortfolioResponse;
import com.finnex.finance_app.domain.portfolio.entity.Portfolio;
import com.finnex.finance_app.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.w3c.dom.stylesheets.LinkStyle;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PortfolioRepository extends JpaRepository<Portfolio, UUID> {
    List<Portfolio> findByUser(User user);
    Optional<Portfolio> findByIdAndUser(UUID id, User user);
}
