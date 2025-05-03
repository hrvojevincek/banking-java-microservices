package com.bankapp.transaction_service.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.bankapp.transaction_service.model.Transaction;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, String> {
    List<Transaction> findByAccountId(String accountId);

    List<Transaction> findBySenderBankId(String senderBankId);

    List<Transaction> findByReceiverBankId(String receiverBankId);

    List<Transaction> findByUserId(String userId);

    Page<Transaction> findByUserIdOrderByCreatedAtDesc(String userId, Pageable pageable);

    List<Transaction> findByUserIdAndCreatedAtBetween(String userId, LocalDateTime start, LocalDateTime end);

    List<Transaction> findByCategory(String category);

    @Query("SELECT t.category, COUNT(t) as count FROM Transaction t WHERE t.userId = ?1 GROUP BY t.category")
    List<Object[]> countByCategory(String userId);
}