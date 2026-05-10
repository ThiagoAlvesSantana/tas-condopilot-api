package com.tas.condopilot.residents.service;

import com.tas.condopilot.common.exception.BusinessException;
import com.tas.condopilot.common.exception.ResourceNotFoundException;
import com.tas.condopilot.residents.dto.ResidentRequest;
import com.tas.condopilot.residents.dto.ResidentResponse;
import com.tas.condopilot.residents.entity.ResidentEntity;
import com.tas.condopilot.residents.repository.ResidentRepository;
import com.tas.condopilot.units.entity.UnitEntity;
import com.tas.condopilot.units.repository.UnitRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ResidentService {

	private final ResidentRepository residentRepository;
	private final UnitRepository unitRepository;

	public ResidentResponse create(ResidentRequest request) {
		if (residentRepository.existsByEmail(request.getEmail())) {
			throw new BusinessException("E-mail do morador já cadastrado");
		}

		UnitEntity unit = unitRepository.findById(request.getUnitId())
				.orElseThrow(() -> new ResourceNotFoundException("Unidade não encontrada"));

		ResidentEntity entity = ResidentEntity.builder().name(request.getName()).email(request.getEmail())
				.phone(request.getPhone()).unit(unit).build();

		return toResponse(residentRepository.save(entity));
	}

	public List<ResidentResponse> findAll() {
		return residentRepository.findAll().stream().map(this::toResponse).toList();
	}

	public ResidentResponse findById(Long id) {
		return toResponse(getEntity(id));
	}

	public ResidentResponse update(Long id, ResidentRequest request) {
		ResidentEntity entity = getEntity(id);

		if (residentRepository.existsByEmailAndIdNot(request.getEmail(), id)) {
			throw new BusinessException("E-mail do morador já cadastrado");
		}

		UnitEntity unit = unitRepository.findById(request.getUnitId())
				.orElseThrow(() -> new ResourceNotFoundException("Unidade não encontrada"));

		entity.setName(request.getName());
		entity.setEmail(request.getEmail());
		entity.setPhone(request.getPhone());
		entity.setUnit(unit);

		return toResponse(residentRepository.save(entity));
	}

	public void delete(Long id) {
		residentRepository.delete(getEntity(id));
	}

	private ResidentEntity getEntity(Long id) {
		return residentRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Morador não encontrado"));
	}

	private ResidentResponse toResponse(ResidentEntity entity) {
		return ResidentResponse.builder().id(entity.getId()).name(entity.getName()).email(entity.getEmail())
				.phone(entity.getPhone()).unitId(entity.getUnit().getId()).unitNumber(entity.getUnit().getNumber())
				.block(entity.getUnit().getBlock()).createdAt(entity.getCreatedAt()).build();
	}
}