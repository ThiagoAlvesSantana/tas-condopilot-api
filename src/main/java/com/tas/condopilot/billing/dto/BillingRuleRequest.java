package com.tas.condopilot.billing.dto;

import java.math.BigDecimal;

public record BillingRuleRequest(
        Long condoId,
        Long transactionCategoryId,
        String name,
        String description,
        BigDecimal amount,
        Integer dueDay,
        Integer daysBeforeDue,
        Boolean autoGenerateBoleto,
        Boolean active
) {}