package com.tas.condopilot.units.controller;

import com.tas.condopilot.units.dto.UnitRequest;
import com.tas.condopilot.units.dto.UnitResponse;
import com.tas.condopilot.units.service.UnitService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/units")
@RequiredArgsConstructor
@Tag(name = "Units")
public class UnitController {

	private final UnitService unitService;

	@PostMapping
	public ResponseEntity<UnitResponse> create(@Valid @RequestBody UnitRequest request) {
		return ResponseEntity.status(HttpStatus.CREATED).body(unitService.create(request));
	}

	@GetMapping
	public ResponseEntity<List<UnitResponse>> findAll() {
		return ResponseEntity.ok(unitService.findAll());
	}

	@GetMapping("/{id}")
	public ResponseEntity<UnitResponse> findById(@PathVariable Long id) {
		return ResponseEntity.ok(unitService.findById(id));
	}

	@PutMapping("/{id}")
	public ResponseEntity<UnitResponse> update(@PathVariable Long id, @Valid @RequestBody UnitRequest request) {
		return ResponseEntity.ok(unitService.update(id, request));
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> delete(@PathVariable Long id) {
		unitService.delete(id);
		return ResponseEntity.noContent().build();
	}
}