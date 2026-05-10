package com.tas.condopilot.reports.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.tas.condopilot.reports.dto.MonthlyFinancialReportResponse;
import com.tas.condopilot.reports.service.MonthlyFinancialReportService;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/financial-reports")
@RequiredArgsConstructor
@Tag(name = "Financial Reports")
public class FinancialReportController {

    private final MonthlyFinancialReportService monthlyFinancialReportService;

    @GetMapping("/monthly")
    public ResponseEntity<MonthlyFinancialReportResponse> monthly(
            @RequestParam Integer month,
            @RequestParam Integer year
    ) {
        return ResponseEntity.ok(monthlyFinancialReportService.generate(month, year));
    }
}