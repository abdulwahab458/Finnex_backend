package com.finnex.finance_app.domain.account.dto.request;

import com.finnex.finance_app.common.enums.AccountType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class CreateAccountRequest {
    @NotBlank
    private  String accountName;
    @NotBlank
    private  String accountNumber;
    @NotNull
    private AccountType accountType;
    @NotBlank
    private String currency;
    @NotNull
    @DecimalMin("0.00")
    private BigDecimal initialBalance;
    
}
