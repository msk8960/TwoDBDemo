package com.example.demo.model;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.util.Date;

@Data
public class Account {

    private Integer accountId;
    private String accountName;
    private Date creationDate;
    private Double accountBalance;

    public Account(Integer accountId, String accountName, Date creationDate, Double accountBalance) {
        this.accountId = accountId;
        this.accountName = accountName;
        this.creationDate = creationDate;
        this.accountBalance = accountBalance;
    }

    public Account()
    {

    }

}
