package com.finnex.finance_app.domain.dashboard.dto;

import com.finnex.finance_app.common.enums.FinancialHealthStatus;
import lombok.Data;

@Data
public class FinancialHealthResponse {

    private Integer score;

    private FinancialHealthStatus status;

}
