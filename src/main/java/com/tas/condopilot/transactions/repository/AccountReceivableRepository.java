package com.tas.condopilot.transactions.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tas.condopilot.transactions.entity.AccountReceivableEntity;

public interface AccountReceivableRepository extends JpaRepository<AccountReceivableEntity, Long> {
	List<AccountReceivableEntity> findAllByOrderByDueDateAsc();

	List<AccountReceivableEntity> findByStatusIdOrderByDueDateAsc(Long statusId);

	boolean existsByTitleAndDueDateAndIsRecurringTrue(String title, LocalDate dueDate);

	boolean existsByUnitIdAndBillingRuleIdAndReferenceMonthAndReferenceYear(Long unitId, Long billingRuleId,
			Integer referenceMonth, Integer referenceYear);

	boolean existsByUnitIdAndReferenceMonthAndReferenceYear(Long unitId, Integer referenceMonth, Integer referenceYear);
}