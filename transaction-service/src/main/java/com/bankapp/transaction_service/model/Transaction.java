package com.bankapp.transaction_service.model;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Data
@Table(name = "transactions")
public class Transaction {
    @Id
    private String id;

    private String name;
    private String paymentChannel;
    private String type;
    private String accountId;
    private double amount;
    private boolean pending;
    private String category;
    private String date;
    private String image;

    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    private String channel;
    private String senderBankId;
    private String receiverBankId;

    private String userId;
    private String status;
    private String referenceId;
    private String description;
    private String currency;
}