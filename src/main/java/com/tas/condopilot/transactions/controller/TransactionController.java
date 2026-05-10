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

import com.tas.condopilot.transactions.dto.TransactionCategoryResponse;
import com.tas.condopilot.transactions.dto.TransactionRequest;
import com.tas.condopilot.transactions.dto.TransactionResponse;
import com.tas.condopilot.transactions.dto.TransactionTypeResponse;
import com.tas.condopilot.transactions.service.TransactionCategoryService;
import com.tas.condopilot.transactions.service.TransactionService;
import com.tas.condopilot.transactions.service.TransactionTypeService;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
@Tag(name = "Transactions")
public class TransactionController {

	private final TransactionService transactionService;
	private final TransactionTypeService transactionTypeService;
	private final TransactionCategoryService transactionCategoryService;

	@GetMapping
	public ResponseEntity<List<TransactionResponse>> findAll() {
		return ResponseEntity.ok(transactionService.findAll());
	}

	@GetMapping("/{id}")
	public ResponseEntity<TransactionResponse> findById(@PathVariable Long id) {
		return ResponseEntity.ok(transactionService.findById(id));
	}

	@PostMapping
	public ResponseEntity<TransactionResponse> create(@Valid @RequestBody TransactionRequest request) {
		return ResponseEntity.status(HttpStatus.CREATED).body(transactionService.create(request));
	}

	@PutMapping("/{id}")
	public ResponseEntity<TransactionResponse> update(@PathVariable Long id,
			@Valid @RequestBody TransactionRequest request) {
		return ResponseEntity.ok(transactionService.update(id, request));
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> delete(@PathVariable Long id) {
		transactionService.delete(id);
		return ResponseEntity.noContent().build();
	}

	@GetMapping("/type")
	public ResponseEntity<List<TransactionTypeResponse>> findAllType() {
		return ResponseEntity.ok(transactionTypeService.findAll());
	}

	@GetMapping("/category")
	public ResponseEntity<List<TransactionCategoryResponse>> findAllCategory(
			@RequestParam(required = false) Long transactionTypeId) {
		return ResponseEntity.ok(transactionCategoryService.findAll(transactionTypeId));
	}
}