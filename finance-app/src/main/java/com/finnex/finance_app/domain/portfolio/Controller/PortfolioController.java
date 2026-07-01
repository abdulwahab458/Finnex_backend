package com.finnex.finance_app.domain.portfolio.Controller;

import com.finnex.finance_app.common.response.ApiResponse;
import com.finnex.finance_app.domain.portfolio.dto.reponse.PortfolioResponse;
import com.finnex.finance_app.domain.portfolio.dto.request.CreatePortfolioRequest;
import com.finnex.finance_app.domain.portfolio.service.PortfolioService;
import com.finnex.finance_app.domain.user.entity.User;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/portfolios")
@RequiredArgsConstructor
public class PortfolioController {
    private  final PortfolioService portfolioService;
    @PostMapping
    public ApiResponse<PortfolioResponse> createPortfolio(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody CreatePortfolioRequest request
            ){
        return ApiResponse.ok(
                portfolioService.createPortfolio(user,request),
                "Portfolio created"
        );
    }
}
