package com.example.demo.model;

import lombok.Data;

import java.util.Date;

@Data
public class Account {

    private Integer accountId;
    private String accountName;
    private Date creationDate;
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
