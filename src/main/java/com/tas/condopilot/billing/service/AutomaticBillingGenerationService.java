package com.tas.condopilot.billing.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tas.condopilot.billing.entity.BillingRuleEntity;
import com.tas.condopilot.billing.repository.BillingRuleRepository;
import com.tas.condopilot.transactions.entity.AccountReceivableEntity;
import com.tas.condopilot.transactions.entity.AccountReceivableItemEntity;
import com.tas.condopilot.transactions.entity.AccountReceivableStatusEntity;
import com.tas.condopilot.transactions.repository.AccountReceivableItemRepository;
import com.tas.condopilot.transactions.repository.AccountReceivableRepository;
import com.tas.condopilot.transactions.repository.AccountReceivableStatusRepository;
import com.tas.condopilot.units.entity.UnitEntity;
import com.tas.condopilot.units.repository.UnitRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AutomaticBillingGenerationService {

    private final BillingRuleRepository billingRuleRepository;
    private final UnitRepository unitRepository;
    private final AccountReceivableRepository accountReceivableRepository;
    private final AccountReceivableItemRepository accountReceivableItemRepository;
    private final AccountReceivableStatusRepository accountReceivableStatusRepository;

    @Transactional
    public void generateForToday() {
        LocalDate today = LocalDate.now();

        AccountReceivableStatusEntity pendingStatus = accountReceivableStatusRepository
                .findAll()
                .stream()
                .filter(status -> "PENDING".equalsIgnoreCase(status.getName()))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Status PENDING não encontrado"));

        List<BillingRuleEntity> activeRules = billingRuleRepository.findByActiveTrue();

        Map<Long, List<BillingRuleEntity>> rulesByCondo = activeRules
                .stream()
                .collect(Collectors.groupingBy(rule -> rule.getCondo().getId()));

        for (Map.Entry<Long, List<BillingRuleEntity>> entry : rulesByCondo.entrySet()) {
            List<BillingRuleEntity> condoRules = entry.getValue();

            if (condoRules.isEmpty()) {
                continue;
            }

            BillingRuleEntity firstRule = condoRules.get(0);

            LocalDate dueDate = buildDueDate(today, firstRule.getDueDay());
            LocalDate generationDate = dueDate.minusDays(firstRule.getDaysBeforeDue());

            if (!today.equals(generationDate)) {
                continue;
            }

            generateConsolidatedReceivablesForCondo(
                    condoRules,
                    dueDate,
                    pendingStatus
            );
        }
    }

    private void generateConsolidatedReceivablesForCondo(
            List<BillingRuleEntity> rules,
            LocalDate dueDate,
            AccountReceivableStatusEntity pendingStatus
    ) {
        BillingRuleEntity firstRule = rules.get(0);

        Long condoId = firstRule.getCondo().getId();
        Integer referenceMonth = dueDate.getMonthValue();
        Integer referenceYear = dueDate.getYear();
        String billingReference = String.format("%02d/%d", referenceMonth, referenceYear);

        BigDecimal totalAmount = rules.stream()
                .map(BillingRuleEntity::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        List<UnitEntity> units = unitRepository.findAll()
                .stream()
                .filter(unit -> unit.getCondo() != null)
                .filter(unit -> unit.getCondo().getId().equals(condoId))
                .toList();

        for (UnitEntity unit : units) {
            boolean alreadyExists =
                    accountReceivableRepository.existsByUnitIdAndReferenceMonthAndReferenceYear(
                            unit.getId(),
                            referenceMonth,
                            referenceYear
                    );

            if (alreadyExists) {
                continue;
            }

            AccountReceivableEntity receivable = new AccountReceivableEntity();
            receivable.setTitle("Mensalidade - " + billingReference);
            receivable.setDescription("Cobrança mensal consolidada do condomínio");
            receivable.setAmount(totalAmount);
            receivable.setDueDate(dueDate);
            receivable.setStatus(pendingStatus);
            receivable.setTransactionCategory(firstRule.getTransactionCategory());
            receivable.setUnit(unit);
            receivable.setBillingRule(null);
            receivable.setReferenceMonth(referenceMonth);
            receivable.setReferenceYear(referenceYear);
            receivable.setBillingReference(billingReference);
            receivable.setIsRecurring(false);
            receivable.setRecurrenceType(null);
            receivable.setDayOfMonth(null);
            receivable.setActive(true);
            receivable.setCreatedAt(LocalDateTime.now());

            AccountReceivableEntity savedReceivable =
                    accountReceivableRepository.save(receivable);

            for (BillingRuleEntity rule : rules) {
                AccountReceivableItemEntity item = new AccountReceivableItemEntity();
                item.setAccountReceivable(savedReceivable);
                item.setBillingRule(rule);
                item.setDescription(rule.getName());
                item.setAmount(rule.getAmount());
                item.setCreatedAt(LocalDateTime.now());

                accountReceivableItemRepository.save(item);
            }
        }
    }

    private LocalDate buildDueDate(LocalDate today, Integer dueDay) {
        int year = today.getYear();
        int month = today.getMonthValue();

        int lastDay = LocalDate.of(year, month, 1).lengthOfMonth();
        int safeDay = Math.min(dueDay, lastDay);

        return LocalDate.of(year, month, safeDay);
    }
}