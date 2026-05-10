package com.tas.condopilot.transactions.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tas.condopilot.transactions.entity.AccountReceivableStatusEntity;

public interface AccountReceivableStatusRepository extends JpaRepository<AccountReceivableStatusEntity, Long> {

    Optional<AccountReceivableStatusEntity> findByName(String name);

}