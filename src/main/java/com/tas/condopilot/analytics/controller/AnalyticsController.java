package com.tas.condopilot.analytics.controller;

import com.tas.condopilot.analytics.dto.DashboardSummaryResponse;
import com.tas.condopilot.analytics.service.AnalyticsService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/analytics")
@RequiredArgsConstructor
@Tag(name = "Analytics")
public class AnalyticsController {

	private final AnalyticsService analyticsService;

	@GetMapping("/dashboard")
	public ResponseEntity<DashboardSummaryResponse> dashboard() {
		return ResponseEntity.ok(analyticsService.getDashboardSummary());
	}
}