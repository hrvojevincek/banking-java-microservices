package com.bankapp.banking.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.bankapp.banking.model.Bank;

@Repository
public interface BankRepository extends JpaRepository<Bank, String> {
    List<Bank> findByUserId(String userId);

    Optional<Bank> findByAccountId(String accountId);

    Optional<Bank> findByShareableId(String shareableId);
}