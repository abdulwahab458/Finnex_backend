package com.finnex.finance_app.domain.dashboard.dto;

import lombok.Data;

@Data
public class FinancialHealthResponse {

    private Integer score;

    private String status;

}
