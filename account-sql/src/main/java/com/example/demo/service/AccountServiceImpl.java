package com.example.demo.service;

import com.example.demo.exception.AccountNotFoundException;
import com.example.demo.exception.CustomerNotActiveException;
import com.example.demo.model.Account;
import com.example.demo.model.AccountDTO;
import com.example.demo.repo.AccountRepo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AccountServiceImpl implements IAccountService {

    private static Logger log = LoggerFactory.getLogger(AccountServiceImpl.class);

    @Autowired
    private AccountRepo accountRepo;

    @Override
    public ResponseEntity<AccountDTO> createAccount(Account account) {
        try {
            log.info("creating account");

            if (!account.getIsCustomerActive()) {
                log.error("customer not active for the account");
                throw new CustomerNotActiveException("customer not active for the account");
            }

            account.setCreationDate(new Date());
            Account savedAccount = accountRepo.save(account);

            log.info("account created");
            return new ResponseEntity<>(new AccountDTO(savedAccount), HttpStatus.CREATED);
        } catch (CustomerNotActiveException e) {
            log.error(e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Override
    public ResponseEntity<List<AccountDTO>> getAllAccounts() {
        try {
            log.info("retrieving list of accounts");
            List<AccountDTO> allAccountDTOs = accountRepo
                    .findAll(Sort.by("accountId").and(Sort.by("accountNumber")))
                    .stream()
                    .map((acc) -> new AccountDTO(acc))
                    .collect(Collectors.toList());

            if (allAccountDTOs.isEmpty()) {
                log.info("list of accounts is empty");
                return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
            }

            log.info("list of accounts retrieved");
            return new ResponseEntity<>(allAccountDTOs, HttpStatus.OK);
        } catch (Exception e) {
            log.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Override
    public ResponseEntity<List<AccountDTO>> getAccountsById(Integer id) {
        try {
            log.info("retrieving accounts by id - {}", id);
            List<AccountDTO> allAccountDTOs = accountRepo
                    .findAllByAccountIdOrderByAccountNumberAsc(id)
                    .stream()
                    .map((acc) -> new AccountDTO(acc))
                    .collect(Collectors.toList());

            if (allAccountDTOs.isEmpty()) {
                log.error("no account found for id - {}", id);
                throw new AccountNotFoundException("no account found for id - " + id);
            }
            log.info("accounts retrieved for id - {}", id);
            return new ResponseEntity<>(allAccountDTOs, HttpStatus.OK);
        } catch (AccountNotFoundException e) {
            log.error(e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
