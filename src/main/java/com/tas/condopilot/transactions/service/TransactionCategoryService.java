package com.tas.condopilot.transactions.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.tas.condopilot.transactions.dto.TransactionCategoryResponse;
import com.tas.condopilot.transactions.entity.TransactionCategoryEntity;
import com.tas.condopilot.transactions.repository.TransactionCategoryRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TransactionCategoryService {

    private final TransactionCategoryRepository repository;

    public List<TransactionCategoryResponse> findAll(Long transactionTypeId) {
        List<TransactionCategoryEntity> categories =
            transactionTypeId == null
                ? repository.findAllByOrderByNameAsc()
                : repository.findByTransactionTypeIdOrderByNameAsc(transactionTypeId);

        return categories.stream()
            .map(category -> new TransactionCategoryResponse(
                category.getId(),
                category.getName(),
                category.getDescription(),
                category.getTransactionType().getId(),
                category.getTransactionType().getName()
            ))
            .toList();
    }
}