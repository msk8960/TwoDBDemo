package com.example.demo.model;

import lombok.Data;

@Data
public class CustomerAccountResponse {
    private Customer customer;
    private Account account;
}
