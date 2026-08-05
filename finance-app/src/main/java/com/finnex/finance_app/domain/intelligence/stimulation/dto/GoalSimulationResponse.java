package com.finnex.finance_app.domain.intelligence.stimulation.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
public class GoalSimulationResponse {

    private String goalName;

    private BigDecimal targetAmount;

    private BigDecimal currentAmount;

    private BigDecimal remainingAmount;

    // Scenario inputs
    private BigDecimal monthlyContribution;

    private BigDecimal lumpSumContribution;

    // Scenario result
    private Integer monthsRequired;

    private LocalDate estimatedCompletionDate;

    private BigDecimal requiredMonthlyContribution;

    private Boolean achievable;

    // Original goal deadline
    private LocalDate targetDate;
}