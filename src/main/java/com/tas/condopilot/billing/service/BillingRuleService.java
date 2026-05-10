package com.tas.condopilot.billing.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tas.condopilot.billing.dto.BillingRuleRequest;
import com.tas.condopilot.billing.dto.BillingRuleResponse;
import com.tas.condopilot.billing.entity.BillingRuleEntity;
import com.tas.condopilot.billing.repository.BillingRuleRepository;
import com.tas.condopilot.condos.entity.CondoEntity;
import com.tas.condopilot.condos.repository.CondoRepository;
import com.tas.condopilot.transactions.entity.TransactionCategoryEntity;
import com.tas.condopilot.transactions.repository.TransactionCategoryRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BillingRuleService {

    private final BillingRuleRepository billingRuleRepository;
    private final CondoRepository condoRepository;
    private final TransactionCategoryRepository transactionCategoryRepository;

    public List<BillingRuleResponse> findAll() {
        return billingRuleRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public BillingRuleResponse findById(Long id) {
        return toResponse(getEntity(id));
    }

    @Transactional
    public BillingRuleResponse create(BillingRuleRequest request) {
        CondoEntity condo = condoRepository.findById(request.condoId())
                .orElseThrow(() -> new RuntimeException("Condomínio não encontrado"));

        TransactionCategoryEntity category = transactionCategoryRepository
                .findById(request.transactionCategoryId())
                .orElseThrow(() -> new RuntimeException("Categoria não encontrada"));

        BillingRuleEntity entity = new BillingRuleEntity();
        entity.setCondo(condo);
        entity.setTransactionCategory(category);
        entity.setName(request.name());
        entity.setDescription(request.description());
        entity.setAmount(request.amount());
        entity.setDueDay(request.dueDay());
        entity.setDaysBeforeDue(request.daysBeforeDue() == null ? 10 : request.daysBeforeDue());
        entity.setAutoGenerateBoleto(
                request.autoGenerateBoleto() == null ? true : request.autoGenerateBoleto()
        );
        entity.setActive(request.active() == null ? true : request.active());
        entity.setCreatedAt(LocalDateTime.now());

        return toResponse(billingRuleRepository.save(entity));
    }

    @Transactional
    public BillingRuleResponse update(Long id, BillingRuleRequest request) {
        BillingRuleEntity entity = getEntity(id);

        CondoEntity condo = condoRepository.findById(request.condoId())
                .orElseThrow(() -> new RuntimeException("Condomínio não encontrado"));

        TransactionCategoryEntity category = transactionCategoryRepository
                .findById(request.transactionCategoryId())
                .orElseThrow(() -> new RuntimeException("Categoria não encontrada"));

        entity.setCondo(condo);
        entity.setTransactionCategory(category);
        entity.setName(request.name());
        entity.setDescription(request.description());
        entity.setAmount(request.amount());
        entity.setDueDay(request.dueDay());
        entity.setDaysBeforeDue(request.daysBeforeDue() == null ? 10 : request.daysBeforeDue());
        entity.setAutoGenerateBoleto(
                request.autoGenerateBoleto() == null ? true : request.autoGenerateBoleto()
        );
        entity.setActive(request.active() == null ? true : request.active());

        return toResponse(billingRuleRepository.save(entity));
    }

    @Transactional
    public void delete(Long id) {
        billingRuleRepository.delete(getEntity(id));
    }

    private BillingRuleEntity getEntity(Long id) {
        return billingRuleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Regra de cobrança não encontrada"));
    }

    private BillingRuleResponse toResponse(BillingRuleEntity entity) {
        return new BillingRuleResponse(
                entity.getId(),
                entity.getCondo().getId(),
                entity.getCondo().getName(),
                entity.getTransactionCategory().getId(),
                entity.getTransactionCategory().getName(),
                entity.getName(),
                entity.getDescription(),
                entity.getAmount(),
                entity.getDueDay(),
                entity.getDaysBeforeDue(),
                entity.getAutoGenerateBoleto(),
                entity.getActive(),
                entity.getCreatedAt()
        );
    }
}