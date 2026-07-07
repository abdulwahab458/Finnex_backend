package com.finnex.finance_app.domain.financial_planning.budgets.entity;

import com.finnex.finance_app.common.audit.Auditable;
import com.finnex.finance_app.common.enums.BudgetPeriod;
import com.finnex.finance_app.common.enums.TransactionCategory;
import com.finnex.finance_app.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;
import org.hibernate.annotations.UuidGenerator;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(
        name = "budgets",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_budget_user_category_period",
                        columnNames = {
                                "user_id",
                                "category",
                                "start_date",
                                "end_date"
                        }
                )
        }
)
@Data
public class Budget extends Auditable {
    @Id
    @GeneratedValue
    @UuidGenerator
    private UUID id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id",nullable = false)
    private User user;
    @Column(nullable = false,length = 100)
    private String name;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransactionCategory category;
    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal targetAmount = BigDecimal.ZERO;

    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal currentSpent = BigDecimal.ZERO;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BudgetPeriod period;

    @Column(nullable = false)
    private LocalDate startDate;

    @Column(nullable = false)
    private LocalDate endDate;




}
