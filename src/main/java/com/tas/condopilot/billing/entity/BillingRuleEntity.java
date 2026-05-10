package com.tas.condopilot.billing.entity;

import java.math.BigDecimal;

import com.tas.condopilot.common.entity.BaseEntity;
import com.tas.condopilot.condos.entity.CondoEntity;
import com.tas.condopilot.transactions.entity.TransactionCategoryEntity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "billing_rules")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BillingRuleEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "condo_id", nullable = false)
    private CondoEntity condo;

    @ManyToOne
    @JoinColumn(name = "transaction_category_id", nullable = false)
    private TransactionCategoryEntity transactionCategory;

    @Column(nullable = false, length = 120)
    private String name;

    private String description;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal amount;

    @Column(nullable = false)
    private Integer dueDay;

    @Column(nullable = false)
    private Integer daysBeforeDue = 10;

    @Column(nullable = false)
    private Boolean autoGenerateBoleto = true;

    @Column(nullable = false)
    private Boolean active = true;
}