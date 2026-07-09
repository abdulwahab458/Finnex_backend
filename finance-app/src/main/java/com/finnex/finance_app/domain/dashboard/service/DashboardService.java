package com.finnex.finance_app.domain.dashboard.service;

import com.finnex.finance_app.domain.dashboard.dto.DashboardResponse;
import com.finnex.finance_app.domain.user.entity.User;

public interface DashboardService {
    DashboardResponse getDashboard(User user);
}
