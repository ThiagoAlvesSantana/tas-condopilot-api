package com.tas.condopilot.billing.scheduler;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.tas.condopilot.billing.service.AutomaticBillingGenerationService;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class BillingScheduler {

    private final AutomaticBillingGenerationService automaticBillingGenerationService;

    @Scheduled(cron = "0 0 6 * * *")
    public void generateMonthlyChargesAutomatically() {
        automaticBillingGenerationService.generateForToday();
    }
}