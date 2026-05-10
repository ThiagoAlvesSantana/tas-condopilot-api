package com.tas.condopilot.transactions.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tas.condopilot.transactions.dto.AccountPayableRequest;
import com.tas.condopilot.transactions.dto.AccountPayableResponse;
import com.tas.condopilot.transactions.dto.AccountPayableStatusResponse;
import com.tas.condopilot.transactions.dto.RecurrenceTypeResponse;
import com.tas.condopilot.transactions.entity.AccountPayableEntity;
import com.tas.condopilot.transactions.entity.AccountPayableStatusEntity;
import com.tas.condopilot.transactions.entity.RecurrenceTypeEntity;
import com.tas.condopilot.transactions.entity.TransactionCategoryEntity;
import com.tas.condopilot.transactions.entity.TransactionEntity;
import com.tas.condopilot.transactions.entity.TransactionTypeEntity;
import com.tas.condopilot.transactions.repository.AccountPayableRepository;
import com.tas.condopilot.transactions.repository.AccountPayableStatusRepository;
import com.tas.condopilot.transactions.repository.RecurrenceTypeRepository;
import com.tas.condopilot.transactions.repository.TransactionCategoryRepository;
import com.tas.condopilot.transactions.repository.TransactionRepository;
import com.tas.condopilot.transactions.repository.TransactionTypeRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AccountPayableService {

    private final AccountPayableRepository accountPayableRepository;
    private final AccountPayableStatusRepository accountPayableStatusRepository;
    private final TransactionCategoryRepository transactionCategoryRepository;
    private final TransactionRepository transactionRepository;
    private final TransactionTypeRepository transactionTypeRepository;
    private final RecurrenceTypeRepository recurrenceTypeRepository;

    public List<AccountPayableResponse> findAll(Long statusId) {
        List<AccountPayableEntity> items;

        if (statusId == null) {
            items = accountPayableRepository.findAllByOrderByDueDateAsc();
        } else {
            items = accountPayableRepository.findByStatusIdOrderByDueDateAsc(statusId);
        }

        return items.stream().map(this::toResponse).toList();
    }

    public AccountPayableResponse findById(Long id) {
        AccountPayableEntity entity = accountPayableRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Conta a pagar não encontrada"));

        return toResponse(entity);
    }

    @Transactional
    public AccountPayableResponse create(AccountPayableRequest request) {
        if (request.installmentTotal() != null && request.installmentTotal() > 1) {
            return createInstallments(request);
        }

        TransactionCategoryEntity category = getCategory(request.transactionCategoryId());
        AccountPayableStatusEntity status = getStatus(request.statusId());

        validateRecurrence(request.isRecurring(), request.recurrenceTypeId(), request.dayOfMonth());

        RecurrenceTypeEntity recurrenceType = null;
        if (Boolean.TRUE.equals(request.isRecurring())) {
            recurrenceType = recurrenceTypeRepository.findById(request.recurrenceTypeId())
                    .orElseThrow(() -> new RuntimeException("Tipo de recorrência não encontrado"));
        }

        AccountPayableEntity entity = new AccountPayableEntity();
        entity.setTitle(request.title());
        entity.setDescription(request.description());
        entity.setAmount(request.amount());
        entity.setDueDate(request.dueDate());
        entity.setStatus(status);
        entity.setTransactionCategory(category);
        entity.setCreatedAt(LocalDateTime.now());

        entity.setIsRecurring(Boolean.TRUE.equals(request.isRecurring()));
        entity.setRecurrenceType(recurrenceType);
        entity.setDayOfMonth(request.dayOfMonth());
        entity.setActive(request.active() == null ? true : request.active());

        entity.setInstallmentNumber(1);
        entity.setInstallmentTotal(1);
        entity.setParentExpense(null);

        return toResponse(accountPayableRepository.save(entity));
    }

    @Transactional
    public AccountPayableResponse update(Long id, AccountPayableRequest request) {
        AccountPayableEntity entity = accountPayableRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Conta a pagar não encontrada"));

        if (isPaid(entity)) {
            throw new RuntimeException("Contas pagas não podem ser editadas");
        }

        TransactionCategoryEntity category = getCategory(request.transactionCategoryId());
        AccountPayableStatusEntity status = getStatus(request.statusId());

        validateRecurrence(request.isRecurring(), request.recurrenceTypeId(), request.dayOfMonth());

        RecurrenceTypeEntity recurrenceType = null;
        if (Boolean.TRUE.equals(request.isRecurring())) {
            recurrenceType = recurrenceTypeRepository.findById(request.recurrenceTypeId())
                    .orElseThrow(() -> new RuntimeException("Tipo de recorrência não encontrado"));
        }

        entity.setTitle(request.title());
        entity.setDescription(request.description());
        entity.setAmount(request.amount());
        entity.setDueDate(request.dueDate());
        entity.setStatus(status);
        entity.setTransactionCategory(category);

        entity.setIsRecurring(Boolean.TRUE.equals(request.isRecurring()));
        entity.setRecurrenceType(recurrenceType);
        entity.setDayOfMonth(request.dayOfMonth());
        entity.setActive(request.active() == null ? true : request.active());

        return toResponse(accountPayableRepository.save(entity));
    }

    @Transactional
    public void delete(Long id) {
        AccountPayableEntity entity = accountPayableRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Conta a pagar não encontrada"));

        if (isPaid(entity)) {
            throw new RuntimeException("Contas pagas não podem ser excluídas");
        }

        accountPayableRepository.delete(entity);
    }

    @Transactional
    public AccountPayableResponse markAsPaid(Long id) {
        AccountPayableEntity entity = accountPayableRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Conta a pagar não encontrada"));

        if (isPaid(entity)) {
            return toResponse(entity);
        }

        AccountPayableStatusEntity paidStatus = accountPayableStatusRepository.findAll()
                .stream()
                .filter(status -> "PAID".equalsIgnoreCase(status.getName()))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Status PAID não encontrado"));

        TransactionTypeEntity expenseType = transactionTypeRepository.findAll()
                .stream()
                .filter(type -> "EXPENSE".equalsIgnoreCase(type.getName()))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Tipo de transação EXPENSE não encontrado"));

        entity.setStatus(paidStatus);
        accountPayableRepository.save(entity);

        TransactionEntity transaction = new TransactionEntity();
        transaction.setTitle("Pagamento: " + entity.getTitle());
        transaction.setDescription(entity.getDescription());
        transaction.setAmount(entity.getAmount());
        transaction.setDate(entity.getDueDate());
        transaction.setTransactionType(expenseType);
        transaction.setTransactionCategory(entity.getTransactionCategory());
        transaction.setAccountPayable(entity);
        transaction.setCreatedAt(LocalDateTime.now());

        transactionRepository.save(transaction);

        return toResponse(entity);
    }

    public List<AccountPayableStatusResponse> findAllStatus() {
        List<AccountPayableStatusEntity> items = accountPayableStatusRepository.findAll();
        return items.stream().map(this::toStatusResponse).toList();
    }

    private AccountPayableResponse createInstallments(AccountPayableRequest request) {
        TransactionCategoryEntity category = getCategory(request.transactionCategoryId());
        AccountPayableStatusEntity status = getStatus(request.statusId());

        Integer totalInstallments = request.installmentTotal();

        if (totalInstallments == null || totalInstallments < 2) {
            throw new RuntimeException("Quantidade de parcelas inválida");
        }

        if (Boolean.TRUE.equals(request.isRecurring())) {
            throw new RuntimeException("Conta parcelada não pode ser recorrente");
        }

        BigDecimal installmentAmount = request.amount()
                .divide(BigDecimal.valueOf(totalInstallments), 2, RoundingMode.HALF_UP);

        AccountPayableEntity parent = null;

        for (int installment = 1; installment <= totalInstallments; installment++) {
            LocalDate installmentDate = request.dueDate().plusMonths(installment - 1);

            AccountPayableEntity entity = new AccountPayableEntity();
            entity.setTitle(request.title() + " " + installment + "/" + totalInstallments);
            entity.setDescription(request.description());
            entity.setAmount(installmentAmount);
            entity.setDueDate(installmentDate);
            entity.setStatus(status);
            entity.setTransactionCategory(category);
            entity.setCreatedAt(LocalDateTime.now());

            entity.setIsRecurring(false);
            entity.setRecurrenceType(null);
            entity.setDayOfMonth(null);
            entity.setActive(true);

            entity.setInstallmentNumber(installment);
            entity.setInstallmentTotal(totalInstallments);
            entity.setParentExpense(parent);

            AccountPayableEntity saved = accountPayableRepository.save(entity);

            if (installment == 1) {
                parent = saved;
            }
        }

        return toResponse(parent);
    }
    
    @Transactional
    public void deleteInstallmentGroup(Long id) {
        AccountPayableEntity selected = accountPayableRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Conta a pagar não encontrada"));

        Long parentId = selected.getParentExpense() != null
                ? selected.getParentExpense().getId()
                : selected.getId();

        List<AccountPayableEntity> installments =
                accountPayableRepository.findByParentExpenseIdOrId(parentId, parentId);

        boolean hasPaidInstallment = installments.stream().anyMatch(this::isPaid);

        if (hasPaidInstallment) {
            throw new RuntimeException("Não é possível cancelar um parcelamento com parcelas já pagas");
        }

        accountPayableRepository.deleteAll(installments);
    }

    private TransactionCategoryEntity getCategory(Long id) {
        return transactionCategoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Categoria não encontrada"));
    }

    private AccountPayableStatusEntity getStatus(Long id) {
        return accountPayableStatusRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Status não encontrado"));
    }

    private boolean isPaid(AccountPayableEntity entity) {
        return entity.getStatus() != null
                && entity.getStatus().getName() != null
                && "PAID".equalsIgnoreCase(entity.getStatus().getName());
    }

    private void validateRecurrence(Boolean isRecurring, Long recurrenceTypeId, Integer dayOfMonth) {
        if (Boolean.TRUE.equals(isRecurring)) {
            if (recurrenceTypeId == null) {
                throw new RuntimeException("Tipo de recorrência é obrigatório");
            }

            if (dayOfMonth == null || dayOfMonth < 1 || dayOfMonth > 31) {
                throw new RuntimeException("Dia do mês inválido para recorrência");
            }
        }
    }

    private AccountPayableResponse toResponse(AccountPayableEntity entity) {
        return new AccountPayableResponse(
                entity.getId(),
                entity.getTitle(),
                entity.getDescription(),
                entity.getAmount(),
                entity.getDueDate(),
                new AccountPayableStatusResponse(
                        entity.getStatus().getId(),
                        entity.getStatus().getName(),
                        entity.getStatus().getDescription(),
                        entity.getStatus().getColor()
                ),
                entity.getTransactionCategory().getId(),
                entity.getTransactionCategory().getName(),
                entity.getIsRecurring(),
                entity.getRecurrenceType() != null
                        ? new RecurrenceTypeResponse(
                                entity.getRecurrenceType().getId(),
                                entity.getRecurrenceType().getName(),
                                entity.getRecurrenceType().getDescription()
                        )
                        : null,
                entity.getDayOfMonth(),
                entity.getActive(),
                entity.getInstallmentNumber(),
                entity.getInstallmentTotal()
        );
    }

    private AccountPayableStatusResponse toStatusResponse(AccountPayableStatusEntity entity) {
        return new AccountPayableStatusResponse(
                entity.getId(),
                entity.getName(),
                entity.getDescription(),
                entity.getColor()
        );
    }
}