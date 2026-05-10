package com.tas.condopilot.transactions.dto;

import java.math.BigDecimal;

public record AccountReceivableItemResponse(
        Long id,
        Long billingRuleId,
        String description,
        BigDecimal amount
) {}