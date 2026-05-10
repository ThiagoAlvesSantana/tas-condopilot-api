package com.tas.condopilot.billing.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tas.condopilot.billing.dto.BillingRuleRequest;
import com.tas.condopilot.billing.dto.BillingRuleResponse;
import com.tas.condopilot.billing.dto.GenerateMonthlyChargesRequest;
import com.tas.condopilot.billing.dto.GenerateMonthlyChargesResponse;
import com.tas.condopilot.billing.service.AutomaticBillingGenerationService;
import com.tas.condopilot.billing.service.BillingRuleService;
import com.tas.condopilot.billing.service.MonthlyChargeGenerationService;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/billing-rules")
@RequiredArgsConstructor
@Tag(name = "Billing Rules")
public class BillingRuleController {

	private final BillingRuleService billingRuleService;
	private final MonthlyChargeGenerationService monthlyChargeGenerationService;
	private final AutomaticBillingGenerationService automaticBillingGenerationService;

	@PostMapping
	public ResponseEntity<BillingRuleResponse> create(@RequestBody BillingRuleRequest request) {
		return ResponseEntity.status(HttpStatus.CREATED).body(billingRuleService.create(request));
	}

	@GetMapping
	public ResponseEntity<List<BillingRuleResponse>> findAll() {
		return ResponseEntity.ok(billingRuleService.findAll());
	}

	@GetMapping("/{id}")
	public ResponseEntity<BillingRuleResponse> findById(@PathVariable Long id) {
		return ResponseEntity.ok(billingRuleService.findById(id));
	}

	@PutMapping("/{id}")
	public ResponseEntity<BillingRuleResponse> update(@PathVariable Long id, @RequestBody BillingRuleRequest request) {
		return ResponseEntity.ok(billingRuleService.update(id, request));
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> delete(@PathVariable Long id) {
		billingRuleService.delete(id);
		return ResponseEntity.noContent().build();
	}

	@PostMapping("/generate-monthly-charges")
	public ResponseEntity<GenerateMonthlyChargesResponse> generateMonthlyCharges(
			@RequestBody GenerateMonthlyChargesRequest request) {
		return ResponseEntity.ok(monthlyChargeGenerationService.generate(request));
	}
	
	@PostMapping("/generate-today")
	public ResponseEntity<Void> generateToday() {
	    automaticBillingGenerationService.generateForToday();
	    return ResponseEntity.noContent().build();
	}
}