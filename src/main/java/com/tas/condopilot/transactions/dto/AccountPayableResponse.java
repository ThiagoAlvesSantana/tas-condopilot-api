package com.tas.condopilot.transactions.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record AccountPayableResponse(
        Long id,
        String title,
        String description,
        BigDecimal amount,
        LocalDate dueDate,
        AccountPayableStatusResponse status,
        Long transactionCategoryId,
        String transactionCategoryName,
        Boolean isRecurring,
        RecurrenceTypeResponse recurrenceType,
        Integer dayOfMonth,
        Boolean active,
        Integer installmentNumber,
        Integer installmentTotal
) {}