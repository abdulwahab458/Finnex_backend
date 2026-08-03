package com.finnex.finance_app.domain.intelligence.tools;

import com.finnex.finance_app.common.enums.PortfolioPerformancePeriod;
import com.finnex.finance_app.domain.intelligence.util.AiSecurityContext;
import com.finnex.finance_app.domain.portfolio.dto.reponse.HoldingResponse;
import com.finnex.finance_app.domain.portfolio.dto.reponse.PortfolioAllocationResponse;
import com.finnex.finance_app.domain.portfolio.dto.reponse.PortfolioPerformanceResponse;
import com.finnex.finance_app.domain.portfolio.dto.reponse.PortfolioResponse;
import com.finnex.finance_app.domain.portfolio.service.PortfolioService;
import com.finnex.finance_app.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class PortfolioTool {

    private final PortfolioService portfolioService;
    private final AiSecurityContext aiSecurityContext;


    // ============================================================
    // 1. ALL PORTFOLIOS
    // ============================================================

    @Tool(description = """
            Get all investment portfolios belonging to the authenticated user.

            Use this tool for questions about:
            - portfolio overview
            - total invested amount
            - total portfolio value
            - portfolio returns
            - portfolio risk levels
            - best or worst portfolio
            - largest or smallest portfolio
            - comparing portfolios
            - number of portfolios
            - overall investment performance

            Each portfolio includes its name, risk level,
            total invested amount, current value and total return percentage.

            Use this tool when the question concerns multiple portfolios
            or the user's overall investment portfolio.
            """)
    public List<PortfolioResponse> getPortfolios() {

        User currentUser =
                aiSecurityContext.getCurrentUser();

        return portfolioService.getPortfolios(currentUser);
    }


    // ============================================================
    // 2. PORTFOLIO HOLDINGS
    // ============================================================

    @Tool(description = """
            Get the stock holdings for one of the authenticated user's
            investment portfolios.

            Use this tool for questions about:
            - stocks owned by the user
            - holdings in a portfolio
            - whether the user owns a stock
            - number of shares owned
            - average purchase cost
            - current stock price
            - current holding value
            - holding return percentage
            - today's stock movement
            - previous closing price
            - best performing holding
            - worst performing holding
            - largest holding
            - smallest holding
            - profitable or losing holdings
            - comparing stocks owned by the user
            - company or sector information for owned stocks

            The portfolioName parameter identifies which portfolio
            should be inspected.

            Match portfolio names case-insensitively.
            """)
    public List<HoldingResponse> getPortfolioHoldings(

            @ToolParam(description = """
                    Name of the user's portfolio.
                    Use the portfolio name provided by the user.
                    """)
            String portfolioName
    ) {

        User currentUser =
                aiSecurityContext.getCurrentUser();

        PortfolioResponse portfolio =
                findPortfolioByName(
                        currentUser,
                        portfolioName
                );

        return portfolioService.getHoldings(
                currentUser,
                portfolio.getId()
        );
    }


    // ============================================================
    // 3. SECTOR ALLOCATION
    // ============================================================

    @Tool(description = """
            Get sector allocation for one of the authenticated user's
            portfolios.

            Use this tool for questions about:
            - portfolio diversification
            - sector allocation
            - sector exposure
            - concentration by sector
            - largest sector
            - smallest sector
            - percentage invested in a sector
            - technology exposure
            - financial sector exposure
            - whether the portfolio is concentrated in one sector

            The result contains each sector's current value and
            percentage of the portfolio.

            Do not invent diversification recommendations or risk
            statistics that are not present in the returned data.
            """)
    public List<PortfolioAllocationResponse> getPortfolioAllocation(

            @ToolParam(description = """
                    Name of the user's portfolio whose sector
                    allocation should be analyzed.
                    """)
            String portfolioName
    ) {

        User currentUser =
                aiSecurityContext.getCurrentUser();

        PortfolioResponse portfolio =
                findPortfolioByName(
                        currentUser,
                        portfolioName
                );

        return portfolioService.getPortfolioAllocation(
                currentUser,
                portfolio.getId()
        );
    }


    // ============================================================
    // 4. HISTORICAL PERFORMANCE
    // ============================================================

    @Tool(description = """
            Get historical performance for one of the authenticated
            user's investment portfolios.

            Use this tool for questions about:
            - historical portfolio performance
            - portfolio value over time
            - one week performance
            - one month performance
            - year-to-date performance
            - complete available performance history
            - highest portfolio value during a period
            - lowest portfolio value during a period
            - portfolio growth or decline over a period
            - portfolio performance trends

            Supported periods:

            W1  = one week
            M1  = one month
            YTD = year to date
            ALL = all available history

            Translate natural language periods to these values.

            Examples:
            "last week" -> W1
            "past week" -> W1
            "last month" -> M1
            "past month" -> M1
            "this year" -> YTD
            "year to date" -> YTD
            "all time" -> ALL
            "full history" -> ALL
            """)
    public PortfolioPerformanceResponse getPortfolioPerformance(

            @ToolParam(description = """
                    Name of the user's portfolio.
                    """)
            String portfolioName,

            @ToolParam(description = """
                    Performance period.

                    Valid values:
                    W1, M1, YTD, ALL.
                    """)
            String period
    ) {

        User currentUser =
                aiSecurityContext.getCurrentUser();

        PortfolioResponse portfolio =
                findPortfolioByName(
                        currentUser,
                        portfolioName
                );

        PortfolioPerformancePeriod performancePeriod;

        try {

            performancePeriod =
                    PortfolioPerformancePeriod.valueOf(
                            period
                                    .trim()
                                    .toUpperCase()
                    );

        } catch (IllegalArgumentException exception) {

            throw new IllegalArgumentException(
                    "Unsupported portfolio performance period: "
                            + period
                            + ". Supported values are W1, M1, YTD and ALL."
            );
        }

        return portfolioService.getPortfolioPeformance(
                currentUser,
                portfolio.getId(),
                performancePeriod
        );
    }


    // ============================================================
    // 5. FIND SPECIFIC HOLDING ACROSS ALL PORTFOLIOS
    // ============================================================

    @Tool(description = """
            Find an owned stock or company across all investment
            portfolios belonging to the authenticated user.

            Use this tool when the user asks about a specific stock
            but does not specify a portfolio.

            Examples:
            - Do I own Apple?
            - Do I have AAPL?
            - How many shares of Microsoft do I own?
            - What is my Tesla holding worth?
            - How is NVDA performing?
            - What price did I buy Apple at?
            - Am I making a profit on Microsoft?
            - Which portfolio contains Tesla?

            Search both the stock symbol and company name.

            This tool searches the user's actual holdings.
            It must not claim the user owns a stock when no matching
            holding is returned.
            """)
    public List<HoldingResponse> findHolding(

            @ToolParam(description = """
                    Stock symbol or company name.

                    Examples:
                    AAPL, Apple, MSFT, Microsoft,
                    TSLA, Tesla, NVDA, Nvidia.
                    """)
            String stockQuery
    ) {

        User currentUser =
                aiSecurityContext.getCurrentUser();

        String normalizedQuery =
                stockQuery
                        .trim()
                        .toLowerCase();

        return portfolioService
                .getPortfolios(currentUser)
                .stream()
                .flatMap(portfolio ->
                        portfolioService
                                .getHoldings(
                                        currentUser,
                                        portfolio.getId()
                                )
                                .stream()
                )
                .filter(holding -> {

                    boolean symbolMatches =
                            holding.getSymbol() != null
                                    && holding.getSymbol()
                                    .toLowerCase()
                                    .contains(normalizedQuery);

                    boolean companyMatches =
                            holding.getCompanyName() != null
                                    && holding.getCompanyName()
                                    .toLowerCase()
                                    .contains(normalizedQuery);

                    return symbolMatches || companyMatches;
                })
                .toList();
    }


    // ============================================================
    // PRIVATE HELPER
    // ============================================================

    private PortfolioResponse findPortfolioByName(
            User currentUser,
            String portfolioName
    ) {

        if (portfolioName == null
                || portfolioName.isBlank()) {

            throw new IllegalArgumentException(
                    "Portfolio name is required."
            );
        }

        String normalizedName =
                portfolioName
                        .trim()
                        .toLowerCase();

        List<PortfolioResponse> portfolios =
                portfolioService.getPortfolios(
                        currentUser
                );

        // First try exact match.
        PortfolioResponse exactMatch =
                portfolios.stream()
                        .filter(portfolio ->
                                portfolio.getName() != null
                                        && portfolio
                                        .getName()
                                        .trim()
                                        .equalsIgnoreCase(
                                                portfolioName.trim()
                                        )
                        )
                        .findFirst()
                        .orElse(null);

        if (exactMatch != null) {
            return exactMatch;
        }

        // Then try partial match.
        List<PortfolioResponse> matches =
                portfolios.stream()
                        .filter(portfolio ->
                                portfolio.getName() != null
                                        && portfolio
                                        .getName()
                                        .toLowerCase()
                                        .contains(normalizedName)
                        )
                        .toList();

        if (matches.isEmpty()) {

            throw new IllegalArgumentException(
                    "No portfolio found matching: "
                            + portfolioName
            );
        }

        if (matches.size() > 1) {

            throw new IllegalArgumentException(
                    "Multiple portfolios match the name: "
                            + portfolioName
                            + ". Please specify the portfolio more precisely."
            );
        }

        return matches.getFirst();
    }
}