package com.tas.condopilot.reports.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;

import com.tas.condopilot.reports.dto.ExpenseReportItem;
import com.tas.condopilot.reports.dto.MonthlyFinancialReportResponse;
import com.tas.condopilot.reports.dto.RevenueReportItem;
import com.tas.condopilot.reports.dto.UnitPaymentReportItem;
import com.tas.condopilot.transactions.entity.AccountPayableEntity;
import com.tas.condopilot.transactions.entity.AccountReceivableEntity;
import com.tas.condopilot.transactions.repository.AccountPayableRepository;
import com.tas.condopilot.transactions.repository.AccountReceivableRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MonthlyFinancialReportService {

    private final AccountReceivableRepository accountReceivableRepository;
    private final AccountPayableRepository accountPayableRepository;

    public MonthlyFinancialReportResponse generate(Integer month, Integer year) {
        validate(month, year);

        LocalDate start = LocalDate.of(year, month, 1);
        LocalDate end = start.withDayOfMonth(start.lengthOfMonth());
        LocalDate today = LocalDate.now();

        List<AccountReceivableEntity> receivables = findReceivablesBetween(start, end);
        List<AccountPayableEntity> payables = findPayablesBetween(start, end);

        List<AccountReceivableEntity> received = receivables.stream()
                .filter(this::isReceived)
                .toList();

        List<AccountReceivableEntity> overdueReceivables = receivables.stream()
                .filter(item -> !isReceived(item))
                .filter(item -> item.getDueDate().isBefore(today))
                .toList();

        List<AccountReceivableEntity> pendingReceivables = receivables.stream()
                .filter(item -> !isReceived(item))
                .filter(item -> !item.getDueDate().isBefore(today))
                .toList();

        List<AccountPayableEntity> paid = payables.stream()
                .filter(this::isPaid)
                .toList();

        List<AccountPayableEntity> overduePayables = payables.stream()
                .filter(item -> !isPaid(item))
                .filter(item -> item.getDueDate().isBefore(today))
                .toList();

        List<AccountPayableEntity> pendingPayables = payables.stream()
                .filter(item -> !isPaid(item))
                .filter(item -> !item.getDueDate().isBefore(today))
                .toList();

        BigDecimal expectedIncome = sumReceivables(receivables);
        BigDecimal receivedIncome = sumReceivables(received);
        BigDecimal overdueIncome = sumReceivables(overdueReceivables);
        BigDecimal pendingIncome = sumReceivables(pendingReceivables);

        BigDecimal paidExpenses = sumPayables(paid);

        BigDecimal openingBalance = calculateOpeningBalance(start);
        BigDecimal closingBalance = openingBalance
                .add(receivedIncome)
                .subtract(paidExpenses);

        BigDecimal defaultRate = calculateDefaultRate(expectedIncome, overdueIncome);

        List<UnitPaymentReportItem> unitItems = receivables.stream()
                .map(item -> new UnitPaymentReportItem(
                        item.getUnit() != null ? item.getUnit().getId() : null,
                        getUnitLabel(item),
                        item.getAmount(),
                        isReceived(item) ? item.getAmount() : BigDecimal.ZERO,
                        isReceived(item) ? BigDecimal.ZERO : item.getAmount(),
                        getReceivableStatusLabel(item, today)
                ))
                .toList();

        List<RevenueReportItem> revenueItems = receivables.stream()
                .map(item -> new RevenueReportItem(
                        item.getId(),
                        item.getTitle(),
                        getUnitLabel(item),
                        item.getAmount(),
                        item.getDueDate(),
                        getReceivableStatusLabel(item, today)
                ))
                .toList();

        List<ExpenseReportItem> expenseItems = payables.stream()
                .map(item -> new ExpenseReportItem(
                        item.getId(),
                        item.getTitle(),
                        item.getTransactionCategory() != null
                                ? item.getTransactionCategory().getName()
                                : "-",
                        item.getAmount(),
                        item.getDueDate(),
                        getPayableStatusLabel(item, today)
                ))
                .toList();

        return new MonthlyFinancialReportResponse(
                String.format("%02d/%d", month, year),

                openingBalance,
                receivedIncome,
                paidExpenses,
                closingBalance,

                expectedIncome,
                pendingIncome,
                overdueIncome,

                defaultRate,

                unitItems,
                revenueItems,
                expenseItems
        );
    }

    private List<AccountReceivableEntity> findReceivablesBetween(LocalDate start, LocalDate end) {
        return accountReceivableRepository.findAll()
                .stream()
                .filter(item -> item.getDueDate() != null)
                .filter(item -> !item.getDueDate().isBefore(start))
                .filter(item -> !item.getDueDate().isAfter(end))
                .toList();
    }

    private List<AccountPayableEntity> findPayablesBetween(LocalDate start, LocalDate end) {
        return accountPayableRepository.findAll()
                .stream()
                .filter(item -> item.getDueDate() != null)
                .filter(item -> !item.getDueDate().isBefore(start))
                .filter(item -> !item.getDueDate().isAfter(end))
                .toList();
    }

    private BigDecimal calculateOpeningBalance(LocalDate start) {
        BigDecimal receivedBeforeMonth = accountReceivableRepository.findAll()
                .stream()
                .filter(this::isReceived)
                .filter(item -> item.getDueDate() != null)
                .filter(item -> item.getDueDate().isBefore(start))
                .map(AccountReceivableEntity::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal paidBeforeMonth = accountPayableRepository.findAll()
                .stream()
                .filter(this::isPaid)
                .filter(item -> item.getDueDate() != null)
                .filter(item -> item.getDueDate().isBefore(start))
                .map(AccountPayableEntity::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return receivedBeforeMonth.subtract(paidBeforeMonth);
    }

    private String getUnitLabel(AccountReceivableEntity entity) {
        if (entity.getUnit() == null) {
            return "-";
        }

        return "Bloco " + entity.getUnit().getBlock()
                + " • Apto " + entity.getUnit().getNumber();
    }

    private String getReceivableStatusLabel(AccountReceivableEntity entity, LocalDate today) {
        if (isReceived(entity)) {
            return "PAGO";
        }

        if (entity.getDueDate() != null && entity.getDueDate().isBefore(today)) {
            return "VENCIDO";
        }

        return "PENDENTE";
    }

    private String getPayableStatusLabel(AccountPayableEntity entity, LocalDate today) {
        if (isPaid(entity)) {
            return "PAGO";
        }

        if (entity.getDueDate() != null && entity.getDueDate().isBefore(today)) {
            return "VENCIDO";
        }

        return "PENDENTE";
    }

    private void validate(Integer month, Integer year) {
        if (month == null || month < 1 || month > 12) {
            throw new RuntimeException("Mês inválido");
        }

        if (year == null || year < 2000) {
            throw new RuntimeException("Ano inválido");
        }
    }

    private boolean isReceived(AccountReceivableEntity entity) {
        return entity.getStatus() != null
                && entity.getStatus().getName() != null
                && (
                    "RECEIVED".equalsIgnoreCase(entity.getStatus().getName())
                            || "PAID".equalsIgnoreCase(entity.getStatus().getName())
                            || "RECEBIDO".equalsIgnoreCase(entity.getStatus().getName())
                );
    }

    private boolean isPaid(AccountPayableEntity entity) {
        return entity.getStatus() != null
                && entity.getStatus().getName() != null
                && (
                    "PAID".equalsIgnoreCase(entity.getStatus().getName())
                            || "PAGO".equalsIgnoreCase(entity.getStatus().getName())
                );
    }

    private BigDecimal sumReceivables(List<AccountReceivableEntity> items) {
        return items.stream()
                .map(AccountReceivableEntity::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private BigDecimal sumPayables(List<AccountPayableEntity> items) {
        return items.stream()
                .map(AccountPayableEntity::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private BigDecimal calculateDefaultRate(
            BigDecimal expectedIncome,
            BigDecimal overdueIncome
    ) {
        if (expectedIncome == null || expectedIncome.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }

        return overdueIncome
                .multiply(BigDecimal.valueOf(100))
                .divide(expectedIncome, 2, RoundingMode.HALF_UP);
    }
}