package com.tas.condopilot.residents.repository;

import com.tas.condopilot.residents.entity.ResidentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ResidentRepository extends JpaRepository<ResidentEntity, Long> {
	boolean existsByEmail(String email);

	boolean existsByEmailAndIdNot(String email, Long id);
}