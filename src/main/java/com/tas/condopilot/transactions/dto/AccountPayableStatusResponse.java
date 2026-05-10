package com.tas.condopilot.transactions.dto;

public record AccountPayableStatusResponse(
        Long id,
        String name,
        String description,
        String color
) {
}