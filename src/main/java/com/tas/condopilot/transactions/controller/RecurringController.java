package com.tas.condopilot.transactions.controller;

import java.util.Map;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tas.condopilot.transactions.service.RecurringGenerationService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/recurring")
@RequiredArgsConstructor
public class RecurringController {

	private final RecurringGenerationService recurringGenerationService;

	@PostMapping("/generate")
	public Map<String, String> generate() {
		recurringGenerationService.generateMonthlyOccurrences();
		return Map.of("message", "Recorrências processadas com sucesso");
	}
}