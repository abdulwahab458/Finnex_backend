package com.finnex.finance_app.domain.dashboard.controller;

import com.finnex.finance_app.common.response.ApiResponse;
import com.finnex.finance_app.domain.dashboard.dto.CashFlowResponse;
import com.finnex.finance_app.domain.dashboard.dto.DashboardResponse;
import com.finnex.finance_app.domain.dashboard.dto.RecentActivityResponse;
import com.finnex.finance_app.domain.dashboard.dto.TopCategoryResponse;
import com.finnex.finance_app.domain.dashboard.service.DashboardService;
import com.finnex.finance_app.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/dashboard")
@RequiredArgsConstructor
public class DashboardController {
    private final DashboardService dashboardService;
    @GetMapping
    public ApiResponse<DashboardResponse> getDashboard(
            @AuthenticationPrincipal User user
    ) {
        return ApiResponse.ok(
                dashboardService.getDashboard(user),
                "Dashboard response fetched successfully"
        );
    }
    @GetMapping("/cash-flow")
    public ApiResponse<CashFlowResponse> getCashFlow(
            @AuthenticationPrincipal User user
    ) {
        return ApiResponse.ok(
                dashboardService.getCashFlow(user),
                "Dashboard response fetched successfully"
        );
    }
    @GetMapping("/top-categories")
    public ApiResponse<List<TopCategoryResponse>> topCategories(
            @AuthenticationPrincipal User user
    ) {
        return ApiResponse.ok(
                dashboardService.getTopCategory(user),
                "Dashboard response fetched successfully"
        );
    }

    @GetMapping("/recent-activity")
    public ApiResponse<List<RecentActivityResponse>> getRecentActivities(
            @AuthenticationPrincipal User user
    ) {

        List<RecentActivityResponse> response =
                dashboardService.getRecentActivities(user);

        return
                ApiResponse.ok(
                        response,
                        "Recent activities fetched successfully."

        );
    }
}
