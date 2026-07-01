package com.finnex.finance_app.domain.portfolio.entity;

import com.finnex.finance_app.common.audit.Auditable;
import com.finnex.finance_app.common.enums.RiskLevel;
import com.finnex.finance_app.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.UuidGenerator;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "portfolios")
@Data
public class Portfolio extends Auditable {
    @Id
    @GeneratedValue
    @UuidGenerator
    private UUID id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;
    @Column(nullable = false)
    private String name;
    @Column(precision = 19, scale = 4)
    private BigDecimal totalInvested;
    @Column(precision = 19, scale = 4)
    private BigDecimal currentValue;
    @Column(precision = 10, scale = 4)
    private BigDecimal totalReturnPercent;
    @Enumerated(EnumType.STRING) @Column(length = 20)
    private RiskLevel riskLevel;



}
