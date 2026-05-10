package com.tas.condopilot.transactions.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record AccountPayableRequest(
        String title,
        String description,
        BigDecimal amount,
        LocalDate dueDate,
        Long statusId,
        Long transactionCategoryId,
        Boolean isRecurring,
        Long recurrenceTypeId,
        Integer dayOfMonth,
        Boolean active,
        Integer installmentTotal
) {}