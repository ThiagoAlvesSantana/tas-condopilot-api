package com.tas.condopilot.transactions.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.tas.condopilot.transactions.dto.TransactionCategoryResponse;
import com.tas.condopilot.transactions.dto.TransactionRequest;
import com.tas.condopilot.transactions.dto.TransactionResponse;
import com.tas.condopilot.transactions.dto.TransactionTypeResponse;
import com.tas.condopilot.transactions.entity.TransactionCategoryEntity;
import com.tas.condopilot.transactions.entity.TransactionEntity;
import com.tas.condopilot.transactions.entity.TransactionTypeEntity;
import com.tas.condopilot.transactions.repository.TransactionCategoryRepository;
import com.tas.condopilot.transactions.repository.TransactionRepository;
import com.tas.condopilot.transactions.repository.TransactionTypeRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final TransactionTypeRepository transactionTypeRepository;
    private final TransactionCategoryRepository transactionCategoryRepository;

    public List<TransactionResponse> findAll() {
        return transactionRepository.findAll()
            .stream()
            .map(this::toResponse)
            .toList();
    }

    public TransactionResponse findById(Long id) {
        TransactionEntity entity = transactionRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Transaction not found"));

        return toResponse(entity);
    }

    public TransactionResponse create(TransactionRequest request) {
        TransactionTypeEntity type = transactionTypeRepository.findById(request.transactionTypeId())
            .orElseThrow(() -> new RuntimeException("Transaction type not found"));

        TransactionCategoryEntity category = transactionCategoryRepository.findById(request.transactionCategoryId())
            .orElseThrow(() -> new RuntimeException("Transaction category not found"));

        validateCategoryBelongsToType(type.getId(), category.getTransactionType().getId());

        TransactionEntity entity = new TransactionEntity();
        entity.setTitle(request.title());
        entity.setDescription(request.description());
        entity.setAmount(request.amount());
        entity.setDate(request.date());
        entity.setTransactionType(type);
        entity.setTransactionCategory(category);
        entity.setCreatedAt(LocalDateTime.now());

        return toResponse(transactionRepository.save(entity));
    }

    public TransactionResponse update(Long id, TransactionRequest request) {
        TransactionEntity entity = transactionRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Transaction not found"));

        if (entity.getAccountPayable() != null || entity.getAccountReceivable() != null) {
            throw new RuntimeException("Não é permitido alterar uma transação gerada automaticamente");
        }

        TransactionTypeEntity type = transactionTypeRepository.findById(request.transactionTypeId())
            .orElseThrow(() -> new RuntimeException("Transaction type not found"));

        TransactionCategoryEntity category = transactionCategoryRepository.findById(request.transactionCategoryId())
            .orElseThrow(() -> new RuntimeException("Transaction category not found"));

        validateCategoryBelongsToType(type.getId(), category.getTransactionType().getId());

        entity.setTitle(request.title());
        entity.setDescription(request.description());
        entity.setAmount(request.amount());
        entity.setDate(request.date());
        entity.setTransactionType(type);
        entity.setTransactionCategory(category);

        return toResponse(transactionRepository.save(entity));
    }

    public void delete(Long id) {
        TransactionEntity entity = transactionRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Transaction not found"));

        if (entity.getAccountPayable() != null || entity.getAccountReceivable() != null) {
            throw new RuntimeException("Não é permitido remover uma transação gerada automaticamente");
        }

        transactionRepository.delete(entity);
    }
    
    private void validateCategoryBelongsToType(Long selectedTypeId, Long categoryTypeId) {
        if (!selectedTypeId.equals(categoryTypeId)) {
            throw new RuntimeException("Transaction category does not belong to selected type");
        }
    }

    private TransactionResponse toResponse(TransactionEntity entity) {
        return new TransactionResponse(
            entity.getId(),
            entity.getTitle(),
            entity.getDescription(),
            entity.getAmount(),
            entity.getDate(),
            new TransactionTypeResponse(
                entity.getTransactionType().getId(),
                entity.getTransactionType().getName(),
                entity.getTransactionType().getDescription()
            ),
            new TransactionCategoryResponse(
                entity.getTransactionCategory().getId(),
                entity.getTransactionCategory().getName(),
                entity.getTransactionCategory().getDescription(),
                entity.getTransactionCategory().getTransactionType().getId(),
                entity.getTransactionCategory().getTransactionType().getName()
            ),
            entity.getAccountPayable() != null ? entity.getAccountPayable().getId() : null,
            entity.getAccountReceivable() != null ? entity.getAccountReceivable().getId() : null
        );
    }
}