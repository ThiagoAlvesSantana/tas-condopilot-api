package com.tas.condopilot.residents.controller;

import com.tas.condopilot.residents.dto.ResidentRequest;
import com.tas.condopilot.residents.dto.ResidentResponse;
import com.tas.condopilot.residents.service.ResidentService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/residents")
@RequiredArgsConstructor
@Tag(name = "Residents")
public class ResidentController {

	private final ResidentService residentService;

	@PostMapping
	public ResponseEntity<ResidentResponse> create(@Valid @RequestBody ResidentRequest request) {
		return ResponseEntity.status(HttpStatus.CREATED).body(residentService.create(request));
	}

	@GetMapping
	public ResponseEntity<List<ResidentResponse>> findAll() {
		return ResponseEntity.ok(residentService.findAll());
	}

	@GetMapping("/{id}")
	public ResponseEntity<ResidentResponse> findById(@PathVariable Long id) {
		return ResponseEntity.ok(residentService.findById(id));
	}

	@PutMapping("/{id}")
	public ResponseEntity<ResidentResponse> update(@PathVariable Long id, @Valid @RequestBody ResidentRequest request) {
		return ResponseEntity.ok(residentService.update(id, request));
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> delete(@PathVariable Long id) {
		residentService.delete(id);
		return ResponseEntity.noContent().build();
	}
}