package com.finnex.finance_app.domain.transactions.entity;

import com.finnex.finance_app.common.audit.Auditable;
import com.finnex.finance_app.common.enums.TransactionCategory;
import com.finnex.finance_app.common.enums.TransactionStatus;
import com.finnex.finance_app.common.enums.TransactionType;
import com.finnex.finance_app.domain.account.entity.Account;
import jakarta.persistence.*;
import org.hibernate.annotations.UuidGenerator;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "transactions")
public class Transaction extends Auditable {
    @Id
    @GeneratedValue
    @UuidGenerator
    private UUID id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id")
    private Account account;
    @Column(nullable = false)
    private LocalDateTime transactionDate;
    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal amount;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransactionType type;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransactionCategory catetory;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransactionStatus status;
    @Column(length = 1000)
    private String notes;
    private String merchantName;
    private String attachmentUrl;

}
