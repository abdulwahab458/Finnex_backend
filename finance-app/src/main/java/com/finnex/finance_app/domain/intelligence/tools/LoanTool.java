package com.finnex.finance_app.domain.intelligence.tools;

import com.finnex.finance_app.domain.intelligence.util.AiSecurityContext;
import com.finnex.finance_app.domain.loans.dto.response.LoanResponse;
import com.finnex.finance_app.domain.loans.service.LoanService;
import com.finnex.finance_app.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class LoanTool {

    private final LoanService loanService;
    private final AiSecurityContext aiSecurityContext;


    @Tool(description = """
            Get all loans belonging to the authenticated user.

            Use this tool when the user asks about:
            - their loans
            - loan overview
            - total debt
            - total outstanding loan balance
            - monthly EMI obligations
            - active or closed loans
            - highest interest rate loan
            - largest outstanding loan
            - which loan ends first
            - which loan ends last

            The returned loan information includes principal amount,
            outstanding balance, interest rate, EMI amount,
            lender, loan type, start date, end date, and status.
            """)
    public List<LoanResponse> getLoans() {

        User currentUser =
                aiSecurityContext.getCurrentUser();

        return loanService.getAllLoans(
                currentUser
        );
    }


    @Tool(description = """
            Find a loan by its name.

            Use this tool when the user asks about a specific named loan.

            Examples:
            - How much do I owe on my Car Loan?
            - What is the EMI for my Home Loan?
            - What is the interest rate on my Education Loan?
            - When does my Car Loan end?
            - Is my Home Loan still active?

            Match the requested loan name with the closest
            existing loan name.
            """)
    public List<LoanResponse> getLoanByName(

            @ToolParam(description = """
                    Name or partial name of the loan.
                    Examples: Car Loan, Home Loan,
                    Education Loan, Personal Loan.
                    """)
            String loanName
    ) {

        User currentUser =
                aiSecurityContext.getCurrentUser();

        return loanService
                .getAllLoans(currentUser)
                .stream()
                .filter(loan ->
                        loan.getLoanName() != null &&
                                loan.getLoanName()
                                        .toLowerCase()
                                        .contains(
                                                loanName
                                                        .trim()
                                                        .toLowerCase()
                                        )
                )
                .toList();
    }
}