package com.tas.condopilot.transactions.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tas.condopilot.transactions.entity.TransactionTypeEntity;

public interface TransactionTypeRepository extends JpaRepository<TransactionTypeEntity, Long> {
	Optional<TransactionTypeEntity> findByName(String name);
}