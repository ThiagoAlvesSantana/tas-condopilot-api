package com.tas.condopilot.transactions.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tas.condopilot.transactions.entity.AccountPayableEntity;

public interface AccountPayableRepository extends JpaRepository<AccountPayableEntity, Long> {

	List<AccountPayableEntity> findAllByOrderByDueDateAsc();

	List<AccountPayableEntity> findByStatusIdOrderByDueDateAsc(Long statusId);
	
	boolean existsByTitleAndDueDateAndIsRecurringTrue(String title, LocalDate dueDate);

	List<AccountPayableEntity> findByParentExpenseIdOrId(Long parentExpenseId, Long id);
}