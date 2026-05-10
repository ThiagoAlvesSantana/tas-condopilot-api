package com.tas.condopilot.transactions.controller;

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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.tas.condopilot.transactions.dto.AccountPayableRequest;
import com.tas.condopilot.transactions.dto.AccountPayableResponse;
import com.tas.condopilot.transactions.dto.AccountPayableStatusResponse;
import com.tas.condopilot.transactions.service.AccountPayableService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/account-payables")
@RequiredArgsConstructor
public class AccountPayableController {

	private final AccountPayableService service;

	@GetMapping
	public ResponseEntity<List<AccountPayableResponse>> findAll(@RequestParam(required = false) Long statusId) {
		return ResponseEntity.ok(service.findAll(statusId));
	}

	@GetMapping("/{id}")
	public ResponseEntity<AccountPayableResponse> findById(@PathVariable Long id) {
		return ResponseEntity.ok(service.findById(id));
	}

	@PostMapping
	public ResponseEntity<AccountPayableResponse> create(@Valid @RequestBody AccountPayableRequest request) {
		return ResponseEntity.status(HttpStatus.CREATED).body(service.create(request));
	}

	@PutMapping("/{id}")
	public ResponseEntity<AccountPayableResponse> update(@PathVariable Long id,
			@Valid @RequestBody AccountPayableRequest request) {
		return ResponseEntity.ok(service.update(id, request));
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> delete(@PathVariable Long id) {
		service.delete(id);
		return ResponseEntity.noContent().build();
	}

	@GetMapping("/status")
	public ResponseEntity<List<AccountPayableStatusResponse>> findAllStatus() {
		return ResponseEntity.ok(service.findAllStatus());
	}

	@PutMapping("/{id}/mark-as-paid")
	public ResponseEntity<AccountPayableResponse> markAsPaid(@PathVariable Long id) {
		return ResponseEntity.ok(service.markAsPaid(id));
	}
	
	@DeleteMapping("/{id}/installments")
	public ResponseEntity<Void> deleteInstallments(@PathVariable Long id) {
		service.deleteInstallmentGroup(id);
	    return ResponseEntity.noContent().build();
	}
}