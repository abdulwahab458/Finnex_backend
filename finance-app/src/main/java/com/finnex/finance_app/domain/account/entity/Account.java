package com.finnex.finance_app.domain.account.entity;

import com.finnex.finance_app.common.audit.Auditable;
import com.finnex.finance_app.common.enums.AccountType;
import com.finnex.finance_app.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.UuidGenerator;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "accounts")
@Data
public class Account extends Auditable {
    @Id
    @GeneratedValue
    @UuidGenerator
    private UUID id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;
    @Column(unique = true)
    private String accountNumber;
    @Enumerated(EnumType.STRING)
    private AccountType accountType;
    @Column(nullable = false)
    private String accountName;
    @Column(precision = 19,scale = 4)
    private BigDecimal balance;
    @Column(precision = 19,scale = 4)
    private BigDecimal availableBalance;
    @Column(length = 3)
    private String currency;
    @Column(nullable = false)
    private Boolean isActive;
    private LocalDate openedDate;
    private LocalDate closedDate;




}
