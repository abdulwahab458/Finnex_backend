package com.finnex.finance_app.domain.account.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UpdateAccountRequest {
    @NotBlank
    private String accountName;
}
