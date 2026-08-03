package com.finnex.finance_app.domain.intelligence.tools;

import com.finnex.finance_app.common.enums.TransactionCategory;
import com.finnex.finance_app.domain.financial_planning.budgets.dto.response.BudgetResponse;
import com.finnex.finance_app.domain.financial_planning.budgets.service.BudgetService;
import com.finnex.finance_app.domain.intelligence.util.AiSecurityContext;
import com.finnex.finance_app.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class BudgetTool {

    private final BudgetService budgetService;
    private final AiSecurityContext aiSecurityContext;


    @Tool(description = """
            Get all budgets belonging to the authenticated user.

            Use this tool when the user asks about:
            - their budgets
            - budget overview
            - budget status
            - remaining budget
            - budgets that are close to their limits
            - budgets that have been exceeded
            - how much they can still spend
            - which budgets are on track

            The returned budget information includes the target amount,
            amount already spent, remaining amount, progress percentage,
            status, category, and budget period.
            """)
    public List<BudgetResponse> getBudgets() {

        User currentUser =
                aiSecurityContext.getCurrentUser();

        return budgetService.getAllBudgets(
                currentUser
        );
    }


    @Tool(description = """
            Get budgets belonging to a specific transaction category.

            Use this tool when the user asks about the budget for a
            particular category.

            Examples:
            - How is my food budget doing?
            - How much of my shopping budget is left?
            - Have I exceeded my entertainment budget?
            - What is my grocery budget?
            - How much can I still spend on food?

            Translate natural-language categories to the closest
            supported TransactionCategory.
            """)
    public List<BudgetResponse> getBudgetsByCategory(

            @ToolParam(description = """
                    Budget transaction category.
                    Examples include FOOD_AND_DINING, SHOPPING,
                    GROCERIES, TRANSPORT, HEALTHCARE, UTILITIES,
                    ENTERTAINMENT, EDUCATION, RENT and OTHER.
                    """)
            String category
    ) {

        User currentUser =
                aiSecurityContext.getCurrentUser();

        TransactionCategory transactionCategory;

        try {

            transactionCategory =
                    TransactionCategory.valueOf(
                            category
                                    .trim()
                                    .toUpperCase()
                    );

        } catch (IllegalArgumentException exception) {

            throw new IllegalArgumentException(
                    "Unsupported budget category: "
                            + category
            );
        }

        return budgetService
                .getAllBudgets(currentUser)
                .stream()
                .filter(budget ->
                        budget.getCategory()
                                == transactionCategory
                )
                .toList();
    }
}