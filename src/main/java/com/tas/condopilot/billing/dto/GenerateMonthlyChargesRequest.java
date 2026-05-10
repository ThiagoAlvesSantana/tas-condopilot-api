package com.tas.condopilot.billing.dto;

public record GenerateMonthlyChargesRequest(
        Long condoId,
        Integer referenceMonth,
        Integer referenceYear
) {}