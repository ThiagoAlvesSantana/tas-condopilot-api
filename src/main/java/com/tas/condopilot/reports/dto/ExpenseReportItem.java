package com.tas.condopilot.reports.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record ExpenseReportItem(
        Long id,
        String title,
        String categoryName,
        BigDecimal amount,
        LocalDate dueDate,
        String status
) {}