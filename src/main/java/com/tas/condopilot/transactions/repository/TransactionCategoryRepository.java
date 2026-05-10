package com.tas.condopilot.transactions.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tas.condopilot.transactions.entity.TransactionCategoryEntity;

@Repository
public interface TransactionCategoryRepository extends JpaRepository<TransactionCategoryEntity, Long> {

	List<TransactionCategoryEntity> findAllByOrderByNameAsc();

	List<TransactionCategoryEntity> findByTransactionTypeIdOrderByNameAsc(Long transactionTypeId);
}