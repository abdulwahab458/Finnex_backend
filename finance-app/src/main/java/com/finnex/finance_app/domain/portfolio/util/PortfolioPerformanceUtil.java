package com.finnex.finance_app.domain.portfolio.util;

import com.finnex.finance_app.common.enums.PortfolioPerformancePeriod;

import java.time.LocalDate;
import java.time.ZoneOffset;

public final class PortfolioPerformanceUtil {

    private PortfolioPerformanceUtil() {
    }

    public static PerformanceDateRange getDateRange(
            PortfolioPerformancePeriod period
    ) {

        LocalDate today = LocalDate.now();

        LocalDate from;
        boolean fullHistory = false;

        switch (period) {

            case W1 -> {
                from = today.minusWeeks(1);
            }

            case M1 -> {
                from = today.minusMonths(1);
            }

            case YTD -> {
                from = LocalDate.of(
                        today.getYear(),
                        1,
                        1
                );
            }

            case ALL -> {
                from = today.minusYears(10);
                fullHistory = true;
            }

            default -> {
                from = today.minusMonths(1);
            }
        }

        long fromUnix =
                from.atStartOfDay(ZoneOffset.UTC)
                        .toEpochSecond();

        long toUnix =
                today.atStartOfDay(ZoneOffset.UTC)
                        .toEpochSecond();

        return new PerformanceDateRange(
                fromUnix,
                toUnix,
                fullHistory
        );
    }
}