package com.tas.condopilot.billing.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tas.condopilot.billing.entity.BillingRuleEntity;

public interface BillingRuleRepository extends JpaRepository<BillingRuleEntity, Long> {
    
	List<BillingRuleEntity> findByActiveTrue();
    
	List<BillingRuleEntity> findByActiveTrueAndDueDay(Integer dueDay);
}