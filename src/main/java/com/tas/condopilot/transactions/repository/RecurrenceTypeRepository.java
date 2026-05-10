package com.tas.condopilot.transactions.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tas.condopilot.transactions.entity.RecurrenceTypeEntity;

public interface RecurrenceTypeRepository extends JpaRepository<RecurrenceTypeEntity, Long> {
	Optional<RecurrenceTypeEntity> findByName(String name);
}