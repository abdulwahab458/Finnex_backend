package com.finnex.finance_app.domain.transactions.dto.request;

import com.finnex.finance_app.common.enums.TransactionCategory;
import com.finnex.finance_app.common.enums.TransactionStatus;
import com.finnex.finance_app.common.enums.TransactionType;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;
@Data
public class CreateTransactionRequest {
    @NotNull
    private UUID accountId;
    @NotNull
    private BigDecimal amount;
    @NotNull
    private TransactionType type;
    @NotNull
    private TransactionCategory category;
    @NotNull
    private TransactionStatus status;
    @NotNull
    private LocalDateTime transactionDate;
    @NotNull
    private  String notes;
    @NotNull
    private String merchantName;


}
