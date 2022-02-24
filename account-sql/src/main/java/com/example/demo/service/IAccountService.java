package com.example.demo.service;

import com.example.demo.model.Account;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface IAccountService {
    public ResponseEntity<Account> createAccount(Account account);

    public ResponseEntity<List<Account>> getAllAccounts();

    public ResponseEntity<List<Account>> getAccountsById(Integer id);
}
