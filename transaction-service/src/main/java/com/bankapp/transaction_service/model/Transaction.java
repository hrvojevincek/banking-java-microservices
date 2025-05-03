package com.bankapp.transaction_service.model;

import java.time.LocalDateTime;

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

    @Column(name = "appwrite_id")
    private String appwriteId;

    private String name;
    private String paymentChannel;
    private String type;
    private String accountId;
    private double amount;
    private boolean pending;
    private String category;
    private String date;
    private String image;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    private String channel;
    private String senderBankId;
    private String receiverBankId;
}