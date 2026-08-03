package com.finnex.finance_app.domain.intelligence.model;


import lombok.Data;
import org.springframework.ai.tool.annotation.ToolParam;

@Data
public class CategorySpendingQuery {

    @ToolParam(description = """
            Transaction category.

            Valid values:
            FOOD_AND_DINING,
            SHOPPING,
            GROCERIES,
            TRANSPORT,
            HEALTHCARE,
            INSURANCE,
            UTILITIES,
            ENTERTAINMENT,
            INVESTMENT,
            DIVIDEND,
            SALARY,
            TRANSFER,
            EDUCATION,
            RENT,
            TAX,
            OTHER.
            """)
    private String category;

    @ToolParam(description = """
            Month to analyze in YYYY-MM format.
            Example: 2026-06 for June 2026.
            """)
    private String month;
}