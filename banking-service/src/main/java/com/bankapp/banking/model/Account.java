package com.bankapp.banking.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Data
@Table(name = "accounts")
public class Account {
    @Id
    private String id;

    private double availableBalance;
    private double currentBalance;
    private String officialName;
    private String mask;
    private String institutionId;
    private String name;
    private String type;
    private String subtype;

    @Column(unique = true)
    private String appwriteItemId;

    @Column(unique = true)
    private String shareableId;
}