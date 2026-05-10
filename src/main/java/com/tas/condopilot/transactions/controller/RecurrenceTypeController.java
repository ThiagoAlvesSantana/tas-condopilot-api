package com.tas.condopilot.transactions.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tas.condopilot.transactions.dto.RecurrenceTypeResponse;
import com.tas.condopilot.transactions.service.RecurrenceTypeService;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/recurrence-types")
@RequiredArgsConstructor
@Tag(name = "Recurrence Type")
public class RecurrenceTypeController {

	private final RecurrenceTypeService recurrenceTypeService;

	@GetMapping
	public List<RecurrenceTypeResponse> findAll() {
		return recurrenceTypeService.findAll();
	}
}