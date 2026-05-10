package com.tas.condopilot.reports.dto;

import java.math.BigDecimal;

public record UnitPaymentReportItem(
        Long unitId,
        String unitLabel,
        BigDecimal chargedAmount,
        BigDecimal paidAmount,
        BigDecimal pendingAmount,
        String status
) {}