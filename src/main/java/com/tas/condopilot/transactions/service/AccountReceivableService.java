package com.tas.condopilot.transactions.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tas.condopilot.transactions.dto.AccountReceivableItemResponse;
import com.tas.condopilot.transactions.dto.AccountReceivableRequest;
import com.tas.condopilot.transactions.dto.AccountReceivableResponse;
import com.tas.condopilot.transactions.dto.AccountReceivableStatusResponse;
import com.tas.condopilot.transactions.dto.RecurrenceTypeResponse;
import com.tas.condopilot.transactions.entity.AccountReceivableEntity;
import com.tas.condopilot.transactions.entity.AccountReceivableStatusEntity;
import com.tas.condopilot.transactions.entity.RecurrenceTypeEntity;
import com.tas.condopilot.transactions.entity.TransactionCategoryEntity;
import com.tas.condopilot.transactions.entity.TransactionEntity;
import com.tas.condopilot.transactions.entity.TransactionTypeEntity;
import com.tas.condopilot.transactions.repository.AccountReceivableItemRepository;
import com.tas.condopilot.transactions.repository.AccountReceivableRepository;
import com.tas.condopilot.transactions.repository.AccountReceivableStatusRepository;
import com.tas.condopilot.transactions.repository.RecurrenceTypeRepository;
import com.tas.condopilot.transactions.repository.TransactionCategoryRepository;
import com.tas.condopilot.transactions.repository.TransactionRepository;
import com.tas.condopilot.transactions.repository.TransactionTypeRepository;
import com.tas.condopilot.units.entity.UnitEntity;
import com.tas.condopilot.units.repository.UnitRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AccountReceivableService {

	private final AccountReceivableRepository accountReceivableRepository;
	private final AccountReceivableItemRepository accountReceivableItemRepository;
	private final AccountReceivableStatusRepository accountReceivableStatusRepository;
	private final TransactionCategoryRepository transactionCategoryRepository;
	private final TransactionRepository transactionRepository;
	private final TransactionTypeRepository transactionTypeRepository;
	private final RecurrenceTypeRepository recurrenceTypeRepository;
	private final UnitRepository unitRepository;

	public List<AccountReceivableResponse> findAll(Long statusId) {
		List<AccountReceivableEntity> items = statusId == null
				? accountReceivableRepository.findAllByOrderByDueDateAsc()
				: accountReceivableRepository.findByStatusIdOrderByDueDateAsc(statusId);

		return items.stream().map(this::toResponse).toList();
	}

	public AccountReceivableResponse findById(Long id) {
		return toResponse(getEntity(id));
	}

	@Transactional
	public AccountReceivableResponse create(AccountReceivableRequest request) {
		TransactionCategoryEntity category = transactionCategoryRepository.findById(request.transactionCategoryId())
				.orElseThrow(() -> new RuntimeException("Categoria não encontrada"));

		AccountReceivableStatusEntity pendingStatus = accountReceivableStatusRepository.findAll().stream()
				.filter(status -> "PENDING".equalsIgnoreCase(status.getName())).findFirst()
				.orElseThrow(() -> new RuntimeException("Status PENDING não encontrado"));

		UnitEntity unit = unitRepository.findById(request.unitId())
				.orElseThrow(() -> new RuntimeException("Unidade não encontrada"));

		RecurrenceTypeEntity recurrenceType = null;
		if (Boolean.TRUE.equals(request.isRecurring())) {
			recurrenceType = recurrenceTypeRepository.findById(request.recurrenceTypeId())
					.orElseThrow(() -> new RuntimeException("Tipo de recorrência não encontrado"));
		}

		AccountReceivableEntity entity = new AccountReceivableEntity();
		entity.setTitle(request.title());
		entity.setDescription(request.description());
		entity.setAmount(request.amount());
		entity.setDueDate(request.dueDate());
		entity.setStatus(pendingStatus);
		entity.setTransactionCategory(category);
		entity.setUnit(unit);
		entity.setCreatedAt(LocalDateTime.now());
		entity.setIsRecurring(Boolean.TRUE.equals(request.isRecurring()));
		entity.setRecurrenceType(recurrenceType);
		entity.setDayOfMonth(request.dayOfMonth());
		entity.setActive(request.active() == null ? true : request.active());

		return toResponse(accountReceivableRepository.save(entity));
	}

	@Transactional
	public AccountReceivableResponse update(Long id, AccountReceivableRequest request) {
		AccountReceivableEntity entity = getEntity(id);

		if (isReceived(entity)) {
			throw new RuntimeException("Contas recebidas não podem ser editadas");
		}

		TransactionCategoryEntity category = transactionCategoryRepository.findById(request.transactionCategoryId())
				.orElseThrow(() -> new RuntimeException("Categoria não encontrada"));

		UnitEntity unit = unitRepository.findById(request.unitId())
				.orElseThrow(() -> new RuntimeException("Unidade não encontrada"));

		RecurrenceTypeEntity recurrenceType = null;
		if (Boolean.TRUE.equals(request.isRecurring())) {
			recurrenceType = recurrenceTypeRepository.findById(request.recurrenceTypeId())
					.orElseThrow(() -> new RuntimeException("Tipo de recorrência não encontrado"));
		}

		entity.setTitle(request.title());
		entity.setDescription(request.description());
		entity.setAmount(request.amount());
		entity.setDueDate(request.dueDate());
		entity.setTransactionCategory(category);
		entity.setUnit(unit);
		entity.setIsRecurring(Boolean.TRUE.equals(request.isRecurring()));
		entity.setRecurrenceType(recurrenceType);
		entity.setDayOfMonth(Boolean.TRUE.equals(request.isRecurring()) ? request.dayOfMonth() : null);
		entity.setActive(request.active() == null ? true : request.active());
		return toResponse(accountReceivableRepository.save(entity));
	}

	@Transactional
	public void delete(Long id) {
		AccountReceivableEntity entity = getEntity(id);

		if (isReceived(entity)) {
			throw new RuntimeException("Contas recebidas não podem ser excluídas");
		}

		accountReceivableRepository.delete(entity);
	}

	public List<AccountReceivableStatusResponse> findAllStatus() {
		return accountReceivableStatusRepository.findAll().stream().map(this::toStatusResponse).toList();
	}

	@Transactional
	public AccountReceivableResponse markAsReceived(Long id) {
		AccountReceivableEntity entity = getEntity(id);

		if (isReceived(entity)) {
			return toResponse(entity);
		}

		AccountReceivableStatusEntity receivedStatus = accountReceivableStatusRepository.findAll().stream().filter(
				status -> "RECEIVED".equalsIgnoreCase(status.getName()) || "PAID".equalsIgnoreCase(status.getName()))
				.findFirst().orElseThrow(() -> new RuntimeException("Status RECEIVED não encontrado"));

		TransactionTypeEntity incomeType = transactionTypeRepository.findAll().stream()
				.filter(type -> "INCOME".equalsIgnoreCase(type.getName())).findFirst()
				.orElseThrow(() -> new RuntimeException("Tipo de transação INCOME não encontrado"));

		entity.setStatus(receivedStatus);
		accountReceivableRepository.save(entity);

		TransactionEntity transaction = new TransactionEntity();
		transaction.setTitle("Recebimento: " + entity.getTitle());
		transaction.setDescription(entity.getDescription());
		transaction.setAmount(entity.getAmount());
		transaction.setDate(entity.getDueDate());
		transaction.setTransactionType(incomeType);
		transaction.setTransactionCategory(entity.getTransactionCategory());
		transaction.setAccountReceivable(entity);
		transaction.setCreatedAt(LocalDateTime.now());

		transactionRepository.save(transaction);

		return toResponse(entity);
	}

	private AccountReceivableEntity getEntity(Long id) {
		return accountReceivableRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("Conta a receber não encontrada"));
	}

	private boolean isReceived(AccountReceivableEntity entity) {
		if (entity.getStatus() == null || entity.getStatus().getName() == null) {
			return false;
		}

		String status = entity.getStatus().getName().trim().toUpperCase();

		return "RECEIVED".equals(status) || "PAID".equals(status);
	}

	private AccountReceivableResponse toResponse(AccountReceivableEntity entity) {
		return new AccountReceivableResponse(entity.getId(), entity.getTitle(), entity.getDescription(),
				entity.getAmount(), entity.getDueDate(),
				new AccountReceivableStatusResponse(entity.getStatus().getId(), entity.getStatus().getName(),
						entity.getStatus().getDescription(), entity.getStatus().getColor()),
				entity.getTransactionCategory().getId(), entity.getTransactionCategory().getName(),

				entity.getUnit() != null ? entity.getUnit().getId() : null,
				entity.getUnit() != null
						? "Bloco " + entity.getUnit().getBlock() + " • Apto " + entity.getUnit().getNumber()
						: null,

				entity.getBillingRule() != null ? entity.getBillingRule().getId() : null, entity.getReferenceMonth(),
				entity.getReferenceYear(), entity.getBillingReference(),

				accountReceivableItemRepository.findByAccountReceivableId(entity.getId()).stream()
						.map(item -> new AccountReceivableItemResponse(item.getId(),
								item.getBillingRule() != null ? item.getBillingRule().getId() : null,
								item.getDescription(), item.getAmount()))
						.toList(),

				entity.getIsRecurring(),
				entity.getRecurrenceType() != null
						? new RecurrenceTypeResponse(entity.getRecurrenceType().getId(),
								entity.getRecurrenceType().getName(), entity.getRecurrenceType().getDescription())
						: null,
				entity.getDayOfMonth(), entity.getActive());
	}

	private AccountReceivableStatusResponse toStatusResponse(AccountReceivableStatusEntity entity) {
		return new AccountReceivableStatusResponse(entity.getId(), entity.getName(), entity.getDescription(),
				entity.getColor());
	}
}