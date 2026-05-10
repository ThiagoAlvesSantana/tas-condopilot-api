package com.tas.condopilot.reports.dto;

import java.math.BigDecimal;
import java.util.List;

public record MonthlyFinancialReportResponse(
        String reference,

        BigDecimal openingBalance,
        BigDecimal totalReceived,
        BigDecimal totalSpent,
        BigDecimal closingBalance,

        BigDecimal expectedIncome,
        BigDecimal pendingIncome,
        BigDecimal overdueIncome,

        BigDecimal defaultRate,

        List<UnitPaymentReportItem> units,
        List<RevenueReportItem> revenues,
        List<ExpenseReportItem> expenses
) {}