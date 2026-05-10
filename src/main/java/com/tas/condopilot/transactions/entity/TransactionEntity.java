package com.tas.condopilot.transactions.entity;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.tas.condopilot.common.entity.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "transactions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransactionEntity extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, length = 120)
	private String title;

	@Column(length = 255)
	private String description;

	@Column(nullable = false, precision = 14, scale = 2)
	private BigDecimal amount;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "transaction_type_id", nullable = false)
	private TransactionTypeEntity transactionType;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "transaction_category_id", nullable = false)
	private TransactionCategoryEntity transactionCategory;

	@Column(nullable = false)
	private LocalDate date;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "account_payable_id", nullable = true)
	private AccountPayableEntity accountPayable;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "account_receivable_id", nullable = true)
	private AccountReceivableEntity accountReceivable;
}