package com.finnex.finance_app.domain.intelligence.tools;

import com.finnex.finance_app.domain.financial_planning.goals.dto.response.GoalResponse;
import com.finnex.finance_app.domain.financial_planning.goals.service.GoalsService;
import com.finnex.finance_app.domain.intelligence.util.AiSecurityContext;
import com.finnex.finance_app.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class GoalTool {

    private final GoalsService goalsService;
    private final AiSecurityContext aiSecurityContext;


    @Tool(description = """
            Get all financial goals belonging to the authenticated user.

            Use this tool when the user asks about:
            - their financial goals
            - goal overview
            - goal progress
            - completed goals
            - goals currently in progress
            - goals that have not been started
            - how much money remains across their goals
            - which goals are closest to completion

            The returned information includes target amount,
            current amount, remaining amount, progress percentage,
            status, category, and target date.
            """)
    public List<GoalResponse> getGoals() {

        User currentUser =
                aiSecurityContext.getCurrentUser();

        return goalsService.findAllGoals(
                currentUser
        );
    }


    @Tool(description = """
            Find a financial goal by its name.

            Use this tool when the user asks about a specific goal.

            Examples:
            - How is my Emergency Fund goal doing?
            - How much is left for my House goal?
            - What is the progress of my Vacation goal?
            - When is my Car goal due?
            - Have I completed my Emergency Fund goal?

            Match the user's requested goal name with the closest
            existing goal name.
            """)
    public List<GoalResponse> getGoalByName(

            @ToolParam(description = """
                    Name or partial name of the financial goal.
                    Examples: Emergency Fund, House, Vacation, Car.
                    """)
            String goalName
    ) {

        User currentUser =
                aiSecurityContext.getCurrentUser();

        return goalsService
                .findAllGoals(currentUser)
                .stream()
                .filter(goal ->
                        goal.getName() != null &&
                                goal.getName()
                                        .toLowerCase()
                                        .contains(
                                                goalName
                                                        .trim()
                                                        .toLowerCase()
                                        )
                )
                .toList();
    }
}