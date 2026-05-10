package com.tas.condopilot.transactions.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record TransactionResponse(
	    Long id,
	    String title,
	    String description,
	    BigDecimal amount,
	    LocalDate date,
	    TransactionTypeResponse type,
	    TransactionCategoryResponse category,
	    Long accountPayableId,
	    Long accountReceivableId
	) {}