package com.example.demo.model;

import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.util.Date;

@Entity
@Table(name="account")
@Data
public class Account {

    @Id
    private Integer accountId;
    @NotBlank(message="name cannot be empty")
    private String accountName;
    private Date creationDate;
    @Enumerated(EnumType.STRING)
    private AccountType accountType;
    private Double accountBalance;

    public Account(Integer accountId, String accountName, Date creationDate,
                   AccountType accountType, Double accountBalance) {
        this.accountId = accountId;
        this.accountName = accountName;
        this.creationDate = creationDate;
        this.accountType = accountType;
        this.accountBalance = accountBalance;
    }

    public Account()
    {

    }

}
