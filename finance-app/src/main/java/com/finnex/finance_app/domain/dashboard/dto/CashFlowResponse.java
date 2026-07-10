package com.finnex.finance_app.domain.dashboard.dto;

import lombok.Data;

import java.util.List;
@Data
public class CashFlowResponse {
    List<CashFlowPointResponse> timeline;
}
