package com.finnex.finance_app.domain.intelligence.stimulation.tool;

import com.finnex.finance_app.domain.financial_planning.goals.dto.response.GoalResponse;
import com.finnex.finance_app.domain.financial_planning.goals.service.GoalsService;

import com.finnex.finance_app.domain.intelligence.stimulation.dto.GoalSimulationResponse;
import com.finnex.finance_app.domain.intelligence.stimulation.service.SimulationService;
import com.finnex.finance_app.domain.intelligence.util.AiSecurityContext;
import com.finnex.finance_app.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Component
@RequiredArgsConstructor
public class SimulationTool {

    private final SimulationService simulationService;
    private final GoalsService goalsService;
    private final AiSecurityContext aiSecurityContext;


    // ============================================================
    // 1. MONTHLY CONTRIBUTION -> COMPLETION DATE
    // ============================================================

    @Tool(description = """
            Simulate when a financial goal will be completed if the user
            contributes a specific amount every month.

            Use this tool for hypothetical or what-if questions such as:
            - If I save 10000 every month, when will I reach my Emergency Fund?
            - What if I invest 15000 monthly toward my House goal?
            - How long will my Vacation goal take if I save 5000 per month?
            - Can I reach my Car goal faster if I save 20000 every month?

            This is a simulation only.
            It does not modify the user's actual financial goal.

            The calculation is performed by Finnex and the returned
            simulation values are authoritative.
            """)
    public GoalSimulationResponse simulateMonthlyContribution(

            @ToolParam(description = """
                    Name or partial name of the user's existing financial goal.
                    Examples: Emergency Fund, House, Vacation, Car.
                    """)
            String goalName,

            @ToolParam(description = """
                    Hypothetical amount the user plans to contribute
                    every month toward the goal.
                    Must be greater than zero.
                    """)
            BigDecimal monthlyContribution
    ) {

        User currentUser =
                aiSecurityContext.getCurrentUser();

        GoalResponse goal =
                findGoalByName(
                        currentUser,
                        goalName
                );

        return simulationService
                .simulateGoalWithMonthlyContribution(
                        currentUser,
                        goal.getId(),
                        monthlyContribution
                );
    }


    // ============================================================
    // 2. LUMP SUM + MONTHLY CONTRIBUTION -> COMPLETION DATE
    // ============================================================

    @Tool(description = """
            Simulate when a financial goal will be completed if the user
            makes an immediate hypothetical lump-sum contribution and
            then contributes a specific amount every month.

            Use this tool for questions such as:
            - If I put 50000 into my Emergency Fund now and save
              10000 monthly, when will I reach my goal?
            - What if I add 100000 to my House goal now and then
              save 20000 every month?
            - If I invest 25000 now and 5000 monthly, how long
              will my Vacation goal take?

            Both contributions are hypothetical.

            This tool does not modify the actual goal or record
            any contribution.
            """)
    public GoalSimulationResponse simulateLumpSumAndMonthlyContribution(

            @ToolParam(description = """
                    Name or partial name of the user's existing
                    financial goal.
                    """)
            String goalName,

            @ToolParam(description = """
                    Hypothetical amount contributed immediately
                    toward the goal.
                    Must be zero or greater.
                    """)
            BigDecimal lumpSumContribution,

            @ToolParam(description = """
                    Hypothetical amount contributed every month
                    after the lump-sum contribution.
                    Must be greater than zero.
                    """)
            BigDecimal monthlyContribution
    ) {

        User currentUser =
                aiSecurityContext.getCurrentUser();

        GoalResponse goal =
                findGoalByName(
                        currentUser,
                        goalName
                );

        return simulationService
                .simulateGoalWithLumpSumAndMonthlyContribution(
                        currentUser,
                        goal.getId(),
                        lumpSumContribution,
                        monthlyContribution
                );
    }


    // ============================================================
    // 3. DESIRED DATE -> REQUIRED MONTHLY CONTRIBUTION
    // ============================================================

    @Tool(description = """
            Calculate how much the user would need to contribute every
            month to reach an existing financial goal by a desired date.

            Use this tool for questions such as:
            - How much should I save monthly to reach my House goal
              by December 2028?
            - How much do I need to contribute each month to finish
              my Emergency Fund by June 2027?
            - What monthly contribution would let me reach my Car
              goal by January 2029?

            The desired completion date is hypothetical and does not
            change the actual target date stored for the goal.

            Convert natural-language dates into ISO YYYY-MM-DD format
            before calling this tool.
            """)
    public GoalSimulationResponse calculateRequiredMonthlyContribution(

            @ToolParam(description = """
                    Name or partial name of the user's existing
                    financial goal.
                    """)
            String goalName,

            @ToolParam(description = """
                    Desired hypothetical completion date in
                    YYYY-MM-DD format.

                    Example:
                    December 31, 2028 -> 2028-12-31
                    """)
            String desiredCompletionDate
    ) {

        User currentUser =
                aiSecurityContext.getCurrentUser();

        GoalResponse goal =
                findGoalByName(
                        currentUser,
                        goalName
                );

        LocalDate completionDate;

        try {

            completionDate =
                    LocalDate.parse(
                            desiredCompletionDate.trim()
                    );

        } catch (Exception exception) {

            throw new IllegalArgumentException(
                    "Invalid completion date. Expected YYYY-MM-DD format."
            );
        }

        return simulationService
                .calculateRequiredMonthlyContribution(
                        currentUser,
                        goal.getId(),
                        completionDate
                );
    }


    // ============================================================
    // 4. CAN I REACH THIS GOAL WITHIN N MONTHS?
    // ============================================================

    @Tool(description = """
            Check whether an existing financial goal can be reached
            within a specified number of months using a hypothetical
            monthly contribution.

            Use this tool for questions such as:
            - Can I reach my Emergency Fund within 12 months if
              I save 10000 per month?
            - Can I complete my House goal in 3 years if I save
              25000 monthly?
            - Will saving 5000 monthly let me reach my Vacation
              goal within 8 months?

            Convert years into months when necessary.

            Examples:
            1 year -> 12 months
            2 years -> 24 months
            3 years -> 36 months

            This is a simulation only and does not modify the goal.
            """)
    public GoalSimulationResponse checkGoalAchievability(

            @ToolParam(description = """
                    Name or partial name of the user's existing
                    financial goal.
                    """)
            String goalName,

            @ToolParam(description = """
                    Hypothetical monthly contribution.
                    Must be greater than zero.
                    """)
            BigDecimal monthlyContribution,

            @ToolParam(description = """
                    Number of months within which the user wants
                    to reach the goal.

                    Convert years to months before calling this tool.
                    """)
            int months
    ) {

        User currentUser =
                aiSecurityContext.getCurrentUser();

        GoalResponse goal =
                findGoalByName(
                        currentUser,
                        goalName
                );

        return simulationService
                .checkGoalAchievability(
                        currentUser,
                        goal.getId(),
                        monthlyContribution,
                        months
                );
    }


    // ============================================================
    // GOAL RESOLUTION
    // ============================================================

    private GoalResponse findGoalByName(
            User currentUser,
            String goalName
    ) {

        if (goalName == null || goalName.isBlank()) {
            throw new IllegalArgumentException(
                    "Goal name is required."
            );
        }

        String normalizedName =
                goalName
                        .trim()
                        .toLowerCase();

        List<GoalResponse> goals =
                goalsService.findAllGoals(
                        currentUser
                );

        // Exact match first
        GoalResponse exactMatch =
                goals.stream()
                        .filter(goal ->
                                goal.getName() != null
                                        && goal.getName()
                                        .trim()
                                        .equalsIgnoreCase(
                                                goalName.trim()
                                        )
                        )
                        .findFirst()
                        .orElse(null);

        if (exactMatch != null) {
            return exactMatch;
        }

        // Partial match second
        List<GoalResponse> matches =
                goals.stream()
                        .filter(goal ->
                                goal.getName() != null
                                        && goal.getName()
                                        .toLowerCase()
                                        .contains(normalizedName)
                        )
                        .toList();

        if (matches.isEmpty()) {
            throw new IllegalArgumentException(
                    "No financial goal found matching: "
                            + goalName
            );
        }

        if (matches.size() > 1) {
            throw new IllegalArgumentException(
                    "Multiple financial goals match: "
                            + goalName
                            + ". Please specify the goal more precisely."
            );
        }

        return matches.getFirst();
    }
}
