package com.tas.condopilot.transactions.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record AccountReceivableRequest(
	    String title,
	    String description,
	    BigDecimal amount,
	    LocalDate dueDate,
	    Long statusId,
	    Long transactionCategoryId,
	    Long unitId,
	    Boolean isRecurring,
	    Long recurrenceTypeId,
	    Integer dayOfMonth,
	    Boolean active
	) {}