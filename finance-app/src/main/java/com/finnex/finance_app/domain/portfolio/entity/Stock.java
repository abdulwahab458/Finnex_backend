package com.finnex.finance_app.domain.portfolio.entity;

import com.finnex.finance_app.common.audit.Auditable;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.UuidGenerator;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "stocks")
@Data
public class Stock extends Auditable {
    @Id
    @GeneratedValue
    @UuidGenerator
    private UUID id;
    @Column(unique = true,length = 10)
    private String symbol;
    @Column(nullable = false)
    private String companyName;
    @Column(length = 50)  private String sector;
    @Column(precision = 19, scale = 4)  private BigDecimal currentPrice;
    @Column(precision = 10, scale = 4)  private BigDecimal dayChangePercent;
    @Column(precision = 10, scale = 4)  private BigDecimal previousClose;
    @Column(precision = 19, scale = 4)  private BigDecimal marketCap;
}
