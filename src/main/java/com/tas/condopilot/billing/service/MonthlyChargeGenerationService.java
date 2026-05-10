package com.tas.condopilot.billing.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tas.condopilot.billing.dto.GenerateMonthlyChargesRequest;
import com.tas.condopilot.billing.dto.GenerateMonthlyChargesResponse;
import com.tas.condopilot.billing.entity.BillingRuleEntity;
import com.tas.condopilot.billing.repository.BillingRuleRepository;
import com.tas.condopilot.transactions.entity.AccountReceivableEntity;
import com.tas.condopilot.transactions.entity.AccountReceivableStatusEntity;
import com.tas.condopilot.transactions.repository.AccountReceivableRepository;
import com.tas.condopilot.transactions.repository.AccountReceivableStatusRepository;
import com.tas.condopilot.units.entity.UnitEntity;
import com.tas.condopilot.units.repository.UnitRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MonthlyChargeGenerationService {

    private final BillingRuleRepository billingRuleRepository;
    private final UnitRepository unitRepository;
    private final AccountReceivableRepository accountReceivableRepository;
    private final AccountReceivableStatusRepository accountReceivableStatusRepository;

    @Transactional
    public GenerateMonthlyChargesResponse generate(GenerateMonthlyChargesRequest request) {
        validateRequest(request);

        AccountReceivableStatusEntity pendingStatus = accountReceivableStatusRepository
                .findAll()
                .stream()
                .filter(status -> "PENDING".equalsIgnoreCase(status.getName()))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Status PENDING não encontrado"));

        List<BillingRuleEntity> rules = billingRuleRepository.findByActiveTrue()
                .stream()
                .filter(rule -> rule.getCondo().getId().equals(request.condoId()))
                .toList();

        List<UnitEntity> units = unitRepository.findAll()
                .stream()
                .filter(unit -> unit.getCondo() != null)
                .filter(unit -> unit.getCondo().getId().equals(request.condoId()))
                .toList();

        int generated = 0;
        int skipped = 0;

        for (BillingRuleEntity rule : rules) {
            LocalDate dueDate = buildDueDate(
                    request.referenceYear(),
                    request.referenceMonth(),
                    rule.getDueDay()
            );

            for (UnitEntity unit : units) {
                boolean alreadyExists =
                        accountReceivableRepository.existsByUnitIdAndBillingRuleIdAndReferenceMonthAndReferenceYear(
                                unit.getId(),
                                rule.getId(),
                                request.referenceMonth(),
                                request.referenceYear()
                        );

                if (alreadyExists) {
                    skipped++;
                    continue;
                }

                AccountReceivableEntity receivable = new AccountReceivableEntity();
                receivable.setTitle(
                        rule.getName() + " - " +
                                String.format("%02d/%d", request.referenceMonth(), request.referenceYear())
                );
                receivable.setDescription(rule.getDescription());
                receivable.setAmount(rule.getAmount());
                receivable.setDueDate(dueDate);
                receivable.setStatus(pendingStatus);
                receivable.setTransactionCategory(rule.getTransactionCategory());
                receivable.setUnit(unit);
                receivable.setBillingRule(rule);
                receivable.setReferenceMonth(request.referenceMonth());
                receivable.setReferenceYear(request.referenceYear());
                receivable.setIsRecurring(false);
                receivable.setRecurrenceType(null);
                receivable.setDayOfMonth(null);
                receivable.setActive(true);
                receivable.setCreatedAt(LocalDateTime.now());

                accountReceivableRepository.save(receivable);
                generated++;
            }
        }

        return new GenerateMonthlyChargesResponse(generated, skipped);
    }

    private void validateRequest(GenerateMonthlyChargesRequest request) {
        if (request.condoId() == null) {
            throw new RuntimeException("Condomínio é obrigatório");
        }

        if (request.referenceMonth() == null || request.referenceMonth() < 1 || request.referenceMonth() > 12) {
            throw new RuntimeException("Mês de referência inválido");
        }

        if (request.referenceYear() == null || request.referenceYear() < 2000) {
            throw new RuntimeException("Ano de referência inválido");
        }
    }

    private LocalDate buildDueDate(Integer year, Integer month, Integer dueDay) {
        int lastDay = LocalDate.of(year, month, 1).lengthOfMonth();
        int safeDay = Math.min(dueDay, lastDay);

        return LocalDate.of(year, month, safeDay);
    }
}