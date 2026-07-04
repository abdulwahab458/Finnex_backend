package com.finnex.finance_app.domain.portfolio.Controller;

import com.finnex.finance_app.common.response.ApiResponse;
import com.finnex.finance_app.domain.portfolio.dto.reponse.HoldingResponse;
import com.finnex.finance_app.domain.portfolio.dto.reponse.PortfolioAllocationResponse;
import com.finnex.finance_app.domain.portfolio.dto.reponse.PortfolioResponse;
import com.finnex.finance_app.domain.portfolio.dto.request.CreateHoldingRequest;
import com.finnex.finance_app.domain.portfolio.dto.request.CreatePortfolioRequest;
import com.finnex.finance_app.domain.portfolio.dto.request.UpdateHoldingRequest;
import com.finnex.finance_app.domain.portfolio.dto.request.UpdatePortfolioRequest;
import com.finnex.finance_app.domain.portfolio.service.PortfolioService;
import com.finnex.finance_app.domain.user.entity.User;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import org.mapstruct.control.MappingControl;
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

    //Holdings

    @GetMapping("/{portfolioId}/holdings")
    public ApiResponse<List<HoldingResponse>> getHoldings(
            @AuthenticationPrincipal User user,
            @PathVariable UUID portfolioId
    ){
        return ApiResponse.ok(
                portfolioService.getHoldings(user,portfolioId),"Fetched all the holdings"
        );
    }


    @PostMapping("/{portfolioId}/holdings")
    public ApiResponse<HoldingResponse> createHolding(
            @AuthenticationPrincipal User user,
            @PathVariable UUID portfolioId,
            @Valid @RequestBody CreateHoldingRequest request

    ){
        return ApiResponse.ok(
                portfolioService.createHolding(user,portfolioId,request),
                "Portfolio created"
        );
    }

    @PutMapping("/{portfolioId}/holdings/{holdingId}")
    public ApiResponse<HoldingResponse> updateHolding(
            @AuthenticationPrincipal User user,
            @PathVariable UUID portfolioId,
            @PathVariable UUID holdingId,
            @Valid @RequestBody UpdateHoldingRequest request
    ){
        return ApiResponse.ok(
                portfolioService.updateHolding(
                        user,
                        portfolioId,
                        holdingId,
                        request
                ),
                "Portfolio updated"
        );
    }
    @DeleteMapping("/{portfolioId}/holdings/{holdingId}")
    public ApiResponse<Void> deleteHolding(
            @AuthenticationPrincipal User user,
            @PathVariable  UUID portfolioId,
            @PathVariable UUID holdingId
            ){
        portfolioService.deleteHolding(user,portfolioId,holdingId);
        return ApiResponse.ok(null,"Portfolio deleted");
    }

    @GetMapping("/{portfolioId}/allocation")
    public ApiResponse<List<PortfolioAllocationResponse>> getAllocation(
            @AuthenticationPrincipal User user,
            @PathVariable UUID portfolioId
    ){
        return ApiResponse.ok(
                portfolioService.getPortfolioAllocation(user,portfolioId),
                "Portfolio allocation fetched"
        );
    }

}
