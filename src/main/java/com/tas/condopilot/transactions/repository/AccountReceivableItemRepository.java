package com.tas.condopilot.transactions.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tas.condopilot.transactions.entity.AccountReceivableItemEntity;

public interface AccountReceivableItemRepository extends JpaRepository<AccountReceivableItemEntity, Long> {

    List<AccountReceivableItemEntity> findByAccountReceivableId(Long accountReceivableId);
}