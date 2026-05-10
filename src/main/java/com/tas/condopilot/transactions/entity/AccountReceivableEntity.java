package com.tas.condopilot.transactions.entity;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.tas.condopilot.billing.entity.BillingRuleEntity;
import com.tas.condopilot.common.entity.BaseEntity;
import com.tas.condopilot.units.entity.UnitEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "account_receivables")
@Getter
@Setter
public class AccountReceivableEntity extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String title;
	private String description;
	private BigDecimal amount;
	private LocalDate dueDate;

	@ManyToOne
	@JoinColumn(name = "status_id")
	private AccountReceivableStatusEntity status;

	@ManyToOne
	@JoinColumn(name = "transaction_category_id")
	private TransactionCategoryEntity transactionCategory;

	@ManyToOne
	@JoinColumn(name = "recurrence_type_id")
	private RecurrenceTypeEntity recurrenceType;

	private Boolean isRecurring;

	private Integer dayOfMonth;

	private Boolean active;

	@ManyToOne
	@JoinColumn(name = "unit_id")
	private UnitEntity unit;

	@ManyToOne
	@JoinColumn(name = "billing_rule_id")
	private BillingRuleEntity billingRule;

	private Integer referenceMonth;

	private Integer referenceYear;
	
	@Column(name = "billing_reference", length = 20)
	private String billingReference;
}