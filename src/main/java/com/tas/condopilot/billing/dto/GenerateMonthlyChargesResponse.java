package com.tas.condopilot.billing.dto;

public record GenerateMonthlyChargesResponse(
        Integer generatedCount,
        Integer skippedCount
) {}