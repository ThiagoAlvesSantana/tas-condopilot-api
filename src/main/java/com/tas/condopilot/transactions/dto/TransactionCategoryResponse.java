package com.tas.condopilot.transactions.dto;

public record TransactionCategoryResponse(
	    Long id,
	    String name,
	    String description,
	    Long transactionTypeId,
	    String transactionTypeName
	) {}