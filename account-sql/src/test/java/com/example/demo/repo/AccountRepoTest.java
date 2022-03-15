package com.example.demo.repo;

import com.example.demo.model.Account;
import com.example.demo.model.AccountType;
import org.junit.Assert;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class AccountRepoTest {

    @Autowired
    private AccountRepo accountRepo;

    @BeforeEach
    private void initRepo() {
        accountRepo.saveAll(createAccounts());
    }

    @AfterEach
    private void deleteRepo() {
        accountRepo.deleteByAccountId(50);
        accountRepo.deleteByAccountId(40);
        accountRepo.flush();
    }

    @Test
    public void testFindAllByAccountId() {
        List<Account> accounts = accountRepo.findAllByAccountIdOrderByAccountNumberAsc(50);

        Assert.assertEquals(accounts.size(), 2);
        Iterator<Account> accountIterator = accounts.iterator();

        for (Account account : accounts) {
            switch (account.getAccountName()) {
                case "account-test-2":
                    Assert.assertEquals(account.getAccountId(), new Integer(50));
                    Assert.assertEquals(account.getAccountBalance(), new Double(20000.0));
                    Assert.assertEquals(account.getAccountType(), AccountType.CASH);
                    break;
                case "account-test-3":
                    Assert.assertEquals(account.getAccountId(), new Integer(50));
                    Assert.assertEquals(account.getAccountBalance(), new Double(12000.0));
                    Assert.assertEquals(account.getAccountType(), AccountType.CURRENT);
                    break;
                default:
                    Assert.fail("different account name - " + account.getAccountName());
            }
        }
    }

    @Test
    public void testFindAllByAccountIdEmpty() {
        List<Account> accountVal = accountRepo.findAllByAccountIdOrderByAccountNumberAsc(600);

        Assert.assertTrue(accountVal.isEmpty());
    }

    private List<Account> createAccounts() {
        List<Account> accounts = new ArrayList<>();

        Account account1 = new Account();
        account1.setAccountId(40);
        account1.setAccountName("account-test-1");
        account1.setAccountNumber(4010);
        account1.setAccountType(AccountType.CASH);
        account1.setAccountBalance(15000.0);
        account1.setIsCustomerActive(Boolean.TRUE);
        account1.setCreationDate(new Date());
        accounts.add(account1);

        Account account2 = new Account();
        account2.setAccountId(50);
        account2.setAccountName("account-test-2");
        account2.setAccountNumber(5010);
        account2.setAccountType(AccountType.CASH);
        account2.setAccountBalance(20000.0);
        account2.setIsCustomerActive(Boolean.TRUE);
        account2.setCreationDate(new Date());
        accounts.add(account2);

        Account account3 = new Account();
        account3.setAccountId(50);
        account3.setAccountName("account-test-3");
        account3.setAccountNumber(5020);
        account3.setAccountType(AccountType.CURRENT);
        account3.setAccountBalance(12000.0);
        account3.setIsCustomerActive(Boolean.TRUE);
        account3.setCreationDate(new Date());
        accounts.add(account3);

        return accounts;
    }
}

