package com.tas.condopilot.transactions.entity;

import java.math.BigDecimal;

import com.tas.condopilot.billing.entity.BillingRuleEntity;
import com.tas.condopilot.common.entity.BaseEntity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "account_receivable_items")
@Getter
@Setter
public class AccountReceivableItemEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "account_receivable_id", nullable = false)
    private AccountReceivableEntity accountReceivable;

    @ManyToOne
    @JoinColumn(name = "billing_rule_id")
    private BillingRuleEntity billingRule;

    @Column(nullable = false, length = 255)
    private String description;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal amount;
}