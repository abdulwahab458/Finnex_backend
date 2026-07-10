package com.finnex.finance_app.domain.dashboard.service;

import com.finnex.finance_app.domain.dashboard.dto.CashFlowResponse;
import com.finnex.finance_app.domain.dashboard.dto.DashboardResponse;
import com.finnex.finance_app.domain.dashboard.dto.RecentActivityResponse;
import com.finnex.finance_app.domain.dashboard.dto.TopCategoryResponse;
import com.finnex.finance_app.domain.user.entity.User;

import java.util.List;

public interface DashboardService {
    DashboardResponse getDashboard(User user);
    CashFlowResponse getCashFlow(User user);
    List<TopCategoryResponse> getTopCategory(User user);
    List<RecentActivityResponse> getRecentActivities(User user);
}
