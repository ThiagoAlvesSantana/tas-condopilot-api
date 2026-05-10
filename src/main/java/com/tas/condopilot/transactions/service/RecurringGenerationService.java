package com.tas.condopilot.transactions.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tas.condopilot.transactions.entity.AccountPayableEntity;
import com.tas.condopilot.transactions.entity.AccountPayableStatusEntity;
import com.tas.condopilot.transactions.entity.AccountReceivableEntity;
import com.tas.condopilot.transactions.entity.AccountReceivableStatusEntity;
import com.tas.condopilot.transactions.repository.AccountPayableRepository;
import com.tas.condopilot.transactions.repository.AccountPayableStatusRepository;
import com.tas.condopilot.transactions.repository.AccountReceivableRepository;
import com.tas.condopilot.transactions.repository.AccountReceivableStatusRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RecurringGenerationService {

    private final AccountPayableRepository accountPayableRepository;
    private final AccountPayableStatusRepository accountPayableStatusRepository;
    private final AccountReceivableRepository accountReceivableRepository;
    private final AccountReceivableStatusRepository accountReceivableStatusRepository;

    @Transactional
    public void generateMonthlyOccurrences() {
        generatePayables();
        generateReceivables();
    }

    private void generatePayables() {
        List<AccountPayableEntity> recurringItems = accountPayableRepository.findAll().stream()
                .filter(item -> Boolean.TRUE.equals(item.getIsRecurring()))
                .filter(item -> Boolean.TRUE.equals(item.getActive()))
                .filter(item -> item.getRecurrenceType() != null)
                .filter(item -> "MONTHLY".equalsIgnoreCase(item.getRecurrenceType().getName()))
                .toList();

        AccountPayableStatusEntity pendingStatus = accountPayableStatusRepository.findByName("PENDING")
                .orElseThrow(() -> new RuntimeException("Status PENDING não encontrado"));

        for (AccountPayableEntity item : recurringItems) {
            LocalDate nextDueDate = item.getDueDate().plusMonths(1);

            boolean alreadyExists = accountPayableRepository
                    .existsByTitleAndDueDateAndIsRecurringTrue(item.getTitle(), nextDueDate);

            if (alreadyExists) {
                continue;
            }

            AccountPayableEntity next = new AccountPayableEntity();
            next.setTitle(item.getTitle());
            next.setDescription(item.getDescription());
            next.setAmount(item.getAmount());
            next.setDueDate(nextDueDate);
            next.setStatus(pendingStatus);
            next.setTransactionCategory(item.getTransactionCategory());
            next.setCreatedAt(LocalDateTime.now());

            next.setIsRecurring(true);
            next.setRecurrenceType(item.getRecurrenceType());
            next.setDayOfMonth(item.getDayOfMonth());
            next.setActive(item.getActive());

            accountPayableRepository.save(next);
        }
    }

    private void generateReceivables() {
        List<AccountReceivableEntity> recurringItems = accountReceivableRepository.findAll().stream()
                .filter(item -> Boolean.TRUE.equals(item.getIsRecurring()))
                .filter(item -> Boolean.TRUE.equals(item.getActive()))
                .filter(item -> item.getRecurrenceType() != null)
                .filter(item -> "MONTHLY".equalsIgnoreCase(item.getRecurrenceType().getName()))
                .toList();

        AccountReceivableStatusEntity pendingStatus = accountReceivableStatusRepository.findByName("PENDING")
                .orElseThrow(() -> new RuntimeException("Status PENDING não encontrado"));

        for (AccountReceivableEntity item : recurringItems) {
            LocalDate nextDueDate = item.getDueDate().plusMonths(1);

            boolean alreadyExists = accountReceivableRepository
                    .existsByTitleAndDueDateAndIsRecurringTrue(item.getTitle(), nextDueDate);

            if (alreadyExists) {
                continue;
            }

            AccountReceivableEntity next = new AccountReceivableEntity();
            next.setTitle(item.getTitle());
            next.setDescription(item.getDescription());
            next.setAmount(item.getAmount());
            next.setDueDate(nextDueDate);
            next.setStatus(pendingStatus);
            next.setTransactionCategory(item.getTransactionCategory());
            next.setCreatedAt(LocalDateTime.now());

            next.setIsRecurring(true);
            next.setRecurrenceType(item.getRecurrenceType());
            next.setDayOfMonth(item.getDayOfMonth());
            next.setActive(item.getActive());

            accountReceivableRepository.save(next);
        }
    }
}