package com.tas.condopilot.units.repository;

import com.tas.condopilot.units.entity.UnitEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UnitRepository extends JpaRepository<UnitEntity, Long> {

	boolean existsByCondoIdAndBlockAndNumber(Long condoId, String block, String number);
}