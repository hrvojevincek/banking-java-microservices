package com.bankapp.banking.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.bankapp.banking.model.Account;

@Repository
public interface AccountRepository extends JpaRepository<Account, String> {
    List<Account> findByUserId(String userId);

    Optional<Account> findByShareableId(String shareableId);

    Optional<Account> findByAppwriteItemId(String appwriteItemId);
}