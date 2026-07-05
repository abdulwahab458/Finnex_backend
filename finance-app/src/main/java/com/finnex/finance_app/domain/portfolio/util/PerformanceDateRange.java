package com.finnex.finance_app.domain.portfolio.util;

public record PerformanceDateRange(
        long from,
        long to,
        boolean fullHistory
) {}