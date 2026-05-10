package com.tas.condopilot.analytics.service;

import java.math.BigDecimal;

import org.springframework.stereotype.Service;

import com.tas.condopilot.analytics.dto.DashboardSummaryResponse;
import com.tas.condopilot.residents.repository.ResidentRepository;
import com.tas.condopilot.transactions.repository.TransactionRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AnalyticsService {

	private final TransactionRepository transactionRepository;
	private final ResidentRepository residentRepository;

	public DashboardSummaryResponse getDashboardSummary() {
		var transactions = transactionRepository.findAll();

		BigDecimal totalIncome = transactions.stream()
				.filter(t -> t.getTransactionType() != null && "INCOME".equals(t.getTransactionType().getName()))
				.map(t -> t.getAmount()).reduce(BigDecimal.ZERO, BigDecimal::add);

		BigDecimal totalExpense = transactions.stream()
				.filter(t -> t.getTransactionType() != null && "EXPENSE".equals(t.getTransactionType().getName()))
				.map(t -> t.getAmount()).reduce(BigDecimal.ZERO, BigDecimal::add);

		return DashboardSummaryResponse.builder().totalIncome(totalIncome).totalExpense(totalExpense)
				.balance(totalIncome.subtract(totalExpense)).totalResidents(residentRepository.count())
				.totalTransactions((long) transactions.size()).build();
	}

}