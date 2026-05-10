package com.tas.condopilot.transactions.controller;

import java.util.List;

import org.springframework.web.bind.annotation.*;

import com.tas.condopilot.transactions.dto.AccountReceivableRequest;
import com.tas.condopilot.transactions.dto.AccountReceivableResponse;
import com.tas.condopilot.transactions.dto.AccountReceivableStatusResponse;
import com.tas.condopilot.transactions.service.AccountReceivableService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/account-receivables")
@RequiredArgsConstructor
public class AccountReceivableController {

	private final AccountReceivableService accountReceivableService;

	@GetMapping
	public List<AccountReceivableResponse> findAll(@RequestParam(required = false) Long statusId) {
		return accountReceivableService.findAll(statusId);
	}

	@GetMapping("/{id}")
	public AccountReceivableResponse findById(@PathVariable Long id) {
		return accountReceivableService.findById(id);
	}

	@PostMapping
	public AccountReceivableResponse create(@RequestBody AccountReceivableRequest request) {
		return accountReceivableService.create(request);
	}

	@PutMapping("/{id}")
	public AccountReceivableResponse update(@PathVariable Long id, @RequestBody AccountReceivableRequest request) {
		return accountReceivableService.update(id, request);
	}

	@DeleteMapping("/{id}")
	public void delete(@PathVariable Long id) {
		accountReceivableService.delete(id);
	}

	@GetMapping("/status")
	public List<AccountReceivableStatusResponse> findAllStatus() {
		return accountReceivableService.findAllStatus();
	}

	@PatchMapping("/{id}/mark-as-received")
	public AccountReceivableResponse markAsReceived(@PathVariable Long id) {
		return accountReceivableService.markAsReceived(id);
	}
}