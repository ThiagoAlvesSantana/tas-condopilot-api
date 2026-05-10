package com.tas.condopilot.condos.controller;

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

import com.tas.condopilot.condos.dto.CondoRequest;
import com.tas.condopilot.condos.dto.CondoResponse;
import com.tas.condopilot.condos.service.CondoService;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/condos")
@RequiredArgsConstructor
@Tag(name = "Condos")
public class CondoController {
	private final CondoService CondoService;

	@PostMapping
	public ResponseEntity<CondoResponse> create(@Valid @RequestBody CondoRequest request) {
		return ResponseEntity.status(HttpStatus.CREATED).body(CondoService.create(request));
	}

	@GetMapping
	public ResponseEntity<List<CondoResponse>> findAll() {
		return ResponseEntity.ok(CondoService.findAll());
	}

	@GetMapping("/{id}")
	public ResponseEntity<CondoResponse> findById(@PathVariable Long id) {
		return ResponseEntity.ok(CondoService.findById(id));
	}

	@PutMapping("/{id}")
	public ResponseEntity<CondoResponse> update(@PathVariable Long id, @Valid @RequestBody CondoRequest request) {
		return ResponseEntity.ok(CondoService.update(id, request));
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> delete(@PathVariable Long id) {
		CondoService.delete(id);
		return ResponseEntity.noContent().build();
	}
}
