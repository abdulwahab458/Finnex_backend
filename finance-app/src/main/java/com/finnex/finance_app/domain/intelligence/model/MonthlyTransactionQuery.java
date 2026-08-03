package com.finnex.finance_app.domain.intelligence.model;

import lombok.Data;
import org.springframework.ai.tool.annotation.ToolParam;

@Data
public class MonthlyTransactionQuery {

    @ToolParam(description = """
            Month to analyze in YYYY-MM format.
            Example: 2026-06 for June 2026.
            """)
    private String month;
}