package com.bankapp.banking.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Data
@Table(name = "banks")
public class Bank {
    @Id
    private String id;

    private String accountId;
    private String bankId;
    private String accessToken;
    private String fundingSourceUrl;
    private String userId;

    @Column(unique = true)
    private String shareableId;
}