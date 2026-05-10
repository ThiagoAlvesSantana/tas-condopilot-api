package com.tas.condopilot.units.service;

import com.tas.condopilot.common.exception.ResourceNotFoundException;
import com.tas.condopilot.condos.entity.CondoEntity;
import com.tas.condopilot.condos.repository.CondoRepository;
import com.tas.condopilot.units.dto.UnitRequest;
import com.tas.condopilot.units.dto.UnitResponse;
import com.tas.condopilot.units.entity.UnitEntity;
import com.tas.condopilot.units.repository.UnitRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UnitService {

	private final UnitRepository unitRepository;
	private final CondoRepository condoRepository;

	public UnitResponse create(UnitRequest request) {
		if (unitRepository.existsByCondoIdAndBlockAndNumber(request.getCondoId(), request.getBlock(),
				request.getNumber())) {
			throw new IllegalArgumentException("Unidade já cadastrada neste condomínio.");
		}

		CondoEntity condo = condoRepository.findById(request.getCondoId())
				.orElseThrow(() -> new ResourceNotFoundException("Condomínio não encontrado"));

		UnitEntity entity = UnitEntity.builder().number(request.getNumber()).block(request.getBlock())
				.status(request.getStatus()).condo(condo).build();

		return toResponse(unitRepository.save(entity));
	}

	public List<UnitResponse> findAll() {
		return unitRepository.findAll().stream().map(this::toResponse).toList();
	}

	public UnitResponse findById(Long id) {
		return toResponse(getEntity(id));
	}

	public UnitResponse update(Long id, UnitRequest request) {
		UnitEntity entity = getEntity(id);

		CondoEntity condo = condoRepository.findById(request.getCondoId())
				.orElseThrow(() -> new ResourceNotFoundException("Condomínio não encontrado"));

		boolean changedIdentity = !entity.getCondo().getId().equals(request.getCondoId())
				|| !entity.getBlock().equals(request.getBlock()) || !entity.getNumber().equals(request.getNumber());

		if (changedIdentity && unitRepository.existsByCondoIdAndBlockAndNumber(request.getCondoId(), request.getBlock(),
				request.getNumber())) {
			throw new IllegalArgumentException("Unidade já cadastrada neste condomínio.");
		}

		entity.setNumber(request.getNumber());
		entity.setBlock(request.getBlock());
		entity.setStatus(request.getStatus());
		entity.setCondo(condo);

		return toResponse(unitRepository.save(entity));
	}

	public void delete(Long id) {
		unitRepository.delete(getEntity(id));
	}

	private UnitEntity getEntity(Long id) {
		return unitRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Unidade não encontrada"));
	}

	private UnitResponse toResponse(UnitEntity entity) {
		return UnitResponse.builder().id(entity.getId()).number(entity.getNumber()).block(entity.getBlock())
				.status(entity.getStatus()).condoId(entity.getCondo().getId()).condoName(entity.getCondo().getName())
				.createdAt(entity.getCreatedAt()).build();
	}
}