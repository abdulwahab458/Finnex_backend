package com.finnex.finance_app.domain.portfolio.Controller;

import com.finnex.finance_app.common.response.ApiResponse;
import com.finnex.finance_app.domain.portfolio.dto.reponse.PortfolioResponse;
import com.finnex.finance_app.domain.portfolio.dto.request.CreatePortfolioRequest;
import com.finnex.finance_app.domain.portfolio.dto.request.UpdatePortfolioRequest;
import com.finnex.finance_app.domain.portfolio.service.PortfolioService;
import com.finnex.finance_app.domain.user.entity.User;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("api/v1/portfolios")
@RequiredArgsConstructor
public class PortfolioController {
    private  final PortfolioService portfolioService;
    @GetMapping
    public ApiResponse<List<PortfolioResponse>> getAllPortfolios(
            @AuthenticationPrincipal User user
    ){
        return ApiResponse.ok(
                portfolioService.getPortfolios(user),"Fetched all the portfolios"
        );
    }
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

    @GetMapping("/{id}")
    public ApiResponse<PortfolioResponse> getPortfolioById(
            @AuthenticationPrincipal User user,
            @PathVariable UUID id
    ){
        return ApiResponse.ok(
                portfolioService.getPortfolioById(user,id),
                "Portfolio retrieved"
        );
    }

    @PutMapping("/{id}")
    public ApiResponse<PortfolioResponse> updatePortfolio(
            @AuthenticationPrincipal User user,
            @PathVariable UUID id,
            @Valid @RequestBody UpdatePortfolioRequest request
    ){
        return ApiResponse.ok(
                portfolioService.updatePortfolio(user,id,request),
                "Portfolio Updated"
        );
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> deletePortfolio(
            @AuthenticationPrincipal User user,
            @PathVariable UUID id
    ){
        portfolioService.deletePortfolio(user,id);
        return ApiResponse.ok(
                null,
                "Portfolio deleted"
        );
    }

}
