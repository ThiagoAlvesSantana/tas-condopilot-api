package com.tas.condopilot.reports.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record RevenueReportItem(
        Long id,
        String title,
        String unitLabel,
        BigDecimal amount,
        LocalDate dueDate,
        String status
) {}