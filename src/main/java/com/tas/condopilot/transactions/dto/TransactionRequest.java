package com.tas.condopilot.transactions.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record TransactionRequest(
	    @NotBlank String title,
	    String description,
	    @NotNull @DecimalMin("0.01") BigDecimal amount,
	    @NotNull Long transactionTypeId,
	    @NotNull Long transactionCategoryId,
	    @NotNull LocalDate date
	) {}