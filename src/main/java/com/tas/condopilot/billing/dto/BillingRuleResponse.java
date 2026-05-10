package com.tas.condopilot.billing.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record BillingRuleResponse(
        Long id,
        Long condoId,
        String condoName,
        Long transactionCategoryId,
        String transactionCategoryName,
        String name,
        String description,
        BigDecimal amount,
        Integer dueDay,
        Integer daysBeforeDue,
        Boolean autoGenerateBoleto,
        Boolean active,
        LocalDateTime createdAt
) {}