package com.example.demo.controller;

import com.example.demo.exception.CustomerNotActiveException;
import com.example.demo.model.Account;
import com.example.demo.repo.AccountRepo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Slf4j
@RequestMapping("/account")
@RestController
public class AccountController {

    @Autowired
    AccountRepo accountRepo;

    @PostMapping("/create")
    public ResponseEntity<Account> createAccount(@Valid @RequestBody Account account) {
        try {
            log.info("creating account");

            if(!account.getIsCustomerActive())
            {
                log.error("customer not active for the account");
                throw new CustomerNotActiveException("customer not active for the account");
            }

            Account savedAccount = accountRepo.save(new Account(
                    account.getAccountId(), account.getAccountName(),
                    new Date(), account.getAccountType(), account.getIsCustomerActive(),
                    account.getAccountBalance()));

            log.info("account created");
            return new ResponseEntity<>(savedAccount, HttpStatus.CREATED);
        } catch (CustomerNotActiveException e) {
            log.error(e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error(e.getMessage());
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/accounts")
    public ResponseEntity<List<Account>> getAllAccounts() {
        try {
            log.info("retrieving list of accounts");
            List<Account> accounts = new ArrayList<>();
            accountRepo.findAll().forEach(accounts::add);

            if (accounts.isEmpty()) {

                log.info("list of accounts is empty");
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }

            log.info("list of accounts retrieved");
            return new ResponseEntity<>(accounts, HttpStatus.OK);
        } catch (Exception e) {
            log.error(e.getMessage());
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/accounts/{id}")
    public ResponseEntity<Account> getAccountById(@PathVariable("id") Integer id) {
        try {
            log.info("retrieving account id - " + id);
            Optional<Account> selectedAccount = accountRepo.findByAccountId(id);
            if (selectedAccount.isPresent())
            {
                if(!selectedAccount.get().getIsCustomerActive()) {
                    log.error("customer is inactive for the account");
                    throw new CustomerNotActiveException("customer is inactive for the account");
                }
                log.info("account id " + id + " retrieved");
                return new ResponseEntity<>(selectedAccount.get(), HttpStatus.OK);
            } else
            {
                log.info("account id " + id + " not found");
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (CustomerNotActiveException e) {
            log.error(e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error(e.getMessage());
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}

