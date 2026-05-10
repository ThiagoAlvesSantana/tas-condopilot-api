package com.tas.condopilot.condos.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.tas.condopilot.common.exception.ResourceNotFoundException;
import com.tas.condopilot.condos.dto.CondoRequest;
import com.tas.condopilot.condos.dto.CondoResponse;
import com.tas.condopilot.condos.entity.CondoEntity;
import com.tas.condopilot.condos.repository.CondoRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CondoService {

	private final CondoRepository CondoRepository;

	public CondoResponse create(CondoRequest request) {

		CondoEntity entity = CondoEntity.builder()
				.name(request.getName()).build();
		entity.setCreatedAt(LocalDateTime.now());
		return toResponse(CondoRepository.save(entity));
	}

	public List<CondoResponse> findAll() {
		return CondoRepository.findAll().stream().map(this::toResponse).toList();
	}

	public CondoResponse findById(Long id) {
		return toResponse(getEntity(id));
	}

	public CondoResponse update(Long id, CondoRequest request) {
		CondoEntity entity = getEntity(id);
		entity.setName(request.getName());
		entity.setCreatedAt(LocalDateTime.now());
		return toResponse(CondoRepository.save(entity));
	}

	public void delete(Long id) {
		CondoRepository.delete(getEntity(id));
	}

	private CondoEntity getEntity(Long id) {
		return CondoRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Condomínuo não encontrado"));
	}

	private CondoResponse toResponse(CondoEntity entity) {
		return CondoResponse.builder()
				.id(entity.getId())
				.name(entity.getName())
				.createdAt(entity.getCreatedAt()).build();
	}
}
