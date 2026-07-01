package com.finnex.finance_app.domain.portfolio.entity;

import com.finnex.finance_app.common.audit.Auditable;
import com.finnex.finance_app.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.UuidGenerator;
import org.springframework.web.bind.annotation.GetMapping;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "stock_holdings")
@Data
public class StockHolding extends Auditable {
    @Id
    @GeneratedValue
    @UuidGenerator
    private UUID id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "portfolio_id")
    private Portfolio portfolio;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "stock_id")
    private Stock stock;
    @Column(nullable = false, precision = 19, scale = 4)
    private int quantity;
    @Column(precision = 19, scale = 4)
    private BigDecimal averageCostBasis;
    @Column(precision = 19, scale = 4)  private BigDecimal currentValue;
    @Column(precision = 10, scale = 4)  private BigDecimal dayChangePercent;
    @Column(precision = 10, scale = 4)  private BigDecimal totalReturnPercent;
}
