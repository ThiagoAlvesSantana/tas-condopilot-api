package com.tas.condopilot.analytics.dto;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@Builder
public class DashboardSummaryResponse {
	private BigDecimal totalIncome;
	private BigDecimal totalExpense;
	private BigDecimal balance;
	private Long totalResidents;
	private Long totalUnits;
	private Long totalBills;
	private Long totalTransactions;
}