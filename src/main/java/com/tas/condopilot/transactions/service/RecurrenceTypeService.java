package com.tas.condopilot.transactions.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.tas.condopilot.transactions.dto.RecurrenceTypeResponse;
import com.tas.condopilot.transactions.entity.RecurrenceTypeEntity;
import com.tas.condopilot.transactions.repository.RecurrenceTypeRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RecurrenceTypeService {

	private final RecurrenceTypeRepository recurrenceTypeRepository;

	public List<RecurrenceTypeResponse> findAll() {
		return recurrenceTypeRepository.findAll().stream().map(this::toResponse).toList();
	}

	private RecurrenceTypeResponse toResponse(RecurrenceTypeEntity entity) {
		return new RecurrenceTypeResponse(entity.getId(), entity.getName(), entity.getDescription());
	}
}