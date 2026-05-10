package com.tas.condopilot.transactions.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.tas.condopilot.transactions.dto.TransactionTypeResponse;
import com.tas.condopilot.transactions.repository.TransactionTypeRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TransactionTypeService {

    private final TransactionTypeRepository repository;

    public List<TransactionTypeResponse> findAll() {
        return repository.findAll()
            .stream()
            .map(type -> new TransactionTypeResponse(
                type.getId(),
                type.getName(),
                type.getDescription()
            ))
            .toList();
    }
}