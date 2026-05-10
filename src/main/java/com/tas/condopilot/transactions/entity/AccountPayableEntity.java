package com.tas.condopilot.transactions.entity;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.tas.condopilot.common.entity.BaseEntity;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "account_payables")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AccountPayableEntity extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String title;

	private String description;

	private BigDecimal amount;

	private LocalDate dueDate;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "status_id")
	private AccountPayableStatusEntity status;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "transaction_category_id")
	private TransactionCategoryEntity transactionCategory;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "recurrence_type_id")
	private RecurrenceTypeEntity recurrenceType;

	private Boolean isRecurring;

	private Integer dayOfMonth;

	private Boolean active;

	@ManyToOne
	@JoinColumn(name = "parent_expense_id")
	private AccountPayableEntity parentExpense;

	private Integer installmentNumber;

	private Integer installmentTotal;
}