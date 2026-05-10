package com.tas.condopilot.transactions.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tas.condopilot.transactions.entity.AccountPayableStatusEntity;

public interface AccountPayableStatusRepository extends JpaRepository<AccountPayableStatusEntity, Long> {

	Optional<AccountPayableStatusEntity> findByName(String name);
	Optional<AccountPayableStatusEntity> findByNameIgnoreCase(String name);
}