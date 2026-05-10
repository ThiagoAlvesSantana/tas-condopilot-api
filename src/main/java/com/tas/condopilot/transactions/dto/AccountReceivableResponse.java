package com.tas.condopilot.transactions.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record AccountReceivableResponse(
        Long id,
        String title,
        String description,
        BigDecimal amount,
        LocalDate dueDate,
        AccountReceivableStatusResponse status,
        Long transactionCategoryId,
        String transactionCategoryName,
        Long unitId,
        String unitLabel,
        Long billingRuleId,
        Integer referenceMonth,
        Integer referenceYear,
        String billingReference,
        List<AccountReceivableItemResponse> items,
        Boolean isRecurring,
        RecurrenceTypeResponse recurrenceType,
        Integer dayOfMonth,
        Boolean active
) {}