package com.bankapp.transaction_service.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.bankapp.transaction_service.model.Transaction;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, String> {
    List<Transaction> findByAccountId(String accountId);

    List<Transaction> findBySenderBankId(String senderBankId);

    List<Transaction> findByReceiverBankId(String receiverBankId);
}