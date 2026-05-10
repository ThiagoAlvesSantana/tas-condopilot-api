package com.tas.condopilot.condos.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tas.condopilot.condos.entity.CondoEntity;

public interface CondoRepository extends JpaRepository<CondoEntity, Long> {

}
