package com.example.demo.service;

import com.example.demo.exception.AccountNotFoundException;
import com.example.demo.exception.CustomerNotActiveException;
import com.example.demo.model.Account;
import com.example.demo.model.AccountDTO;
import com.example.demo.model.AccountType;
import com.example.demo.repo.AccountRepo;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class AccountServiceImplTest {

    @MockBean
    AccountRepo accountRepo;

    @Autowired
    @InjectMocks
    AccountServiceImpl accountService;

    @Test
    public void testCreateAccount() throws Exception {
        Account savedAccount = getDummyAccount();
        savedAccount.setCreationDate(new Date());
        Mockito.when(accountRepo.save(Mockito.any(Account.class)))
                .thenReturn(savedAccount);

        Account toCreateAccount = getDummyAccount();
        ResponseEntity<AccountDTO> returnedAccountResponse = accountService.createAccount(toCreateAccount);
        Assert.assertEquals(returnedAccountResponse.getStatusCode(), HttpStatus.CREATED);
        AccountDTO returnedAccount = returnedAccountResponse.getBody();

        Assert.assertEquals(toCreateAccount.getAccountId(), returnedAccount.getAccountId());
        Assert.assertEquals(toCreateAccount.getAccountNumber(), returnedAccount.getAccountNumber());
        Assert.assertEquals(toCreateAccount.getAccountName(), returnedAccount.getAccountName());
        Assert.assertEquals(toCreateAccount.getAccountType(), returnedAccount.getAccountType());
        Assert.assertEquals(toCreateAccount.getAccountBalance(), returnedAccount.getAccountBalance());
        Assert.assertTrue(returnedAccount.getIsCustomerActive());

        Assert.assertEquals(savedAccount.getCreationDate(), returnedAccount.getCreationDate());
    }

    @Test
    public void testCreateAccountInactive() throws Exception {
        Mockito.when(accountRepo.save(Mockito.any(Account.class)))
                .thenReturn(getDummyAccount());

        Account toCreateAccount = getDummyAccount();
        toCreateAccount.setIsCustomerActive(Boolean.FALSE);

        Assert.assertThrows(CustomerNotActiveException.class, () -> {
            accountService.createAccount(toCreateAccount);
        });
    }

    @Test
    public void testCreateAccountNull() throws Exception {
        Mockito.when(accountRepo.save(Mockito.any(Account.class)))
                .thenReturn(getDummyAccount());

        ResponseEntity response = accountService.createAccount(null);
        Assert.assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

    @Test
    public void testGetAllAccounts() throws Exception {
        Mockito.when(accountRepo.findAll())
                .thenReturn(getAccountList());

        ResponseEntity<List<AccountDTO>> returnedResponse = accountService.getAllAccounts();

        Assert.assertEquals(returnedResponse.getStatusCode(), HttpStatus.OK);
        List<AccountDTO> returnedAccounts = returnedResponse.getBody();
        Integer ids[] = {40, 45, 45, 50};
        Integer accountNumbers[] = {4000, 4500, 4600, 5000};
        AccountType accountTypes[] = {AccountType.CASH, AccountType.CASH, AccountType.CURRENT, AccountType.CASH};
        Double accountBalances[] = {8000.0, 12000.0, 13000.0, 20000.0};
        int i = 0;
        for (AccountDTO returnedAccount : returnedAccounts) {
            Assert.assertEquals(returnedAccount.getAccountId(), ids[i]);
            Assert.assertEquals(returnedAccount.getAccountNumber(), accountNumbers[i]);
            Assert.assertEquals(returnedAccount.getAccountType(), accountTypes[i]);
            Assert.assertEquals(returnedAccount.getAccountBalance(), accountBalances[i]);
            Assert.assertEquals(returnedAccount.getIsCustomerActive(), Boolean.TRUE);
            i++;
        }
    }

    @Test
    public void testGetAllAccountsEmpty() throws Exception {
        Mockito.when(accountRepo.findAll())
                .thenReturn(new ArrayList<Account>());

        ResponseEntity<List<AccountDTO>> returnedResponse = accountService.getAllAccounts();

        Assert.assertEquals(returnedResponse.getStatusCode(), HttpStatus.NO_CONTENT);
        Assert.assertTrue(returnedResponse.getBody() == null);
    }

    @Test
    public void testGetAccountsById() throws Exception {
        Mockito.when(accountRepo.findAllByAccountId(45))
                .thenReturn(getAccountsOfOneCustomer());

        ResponseEntity<List<AccountDTO>> returnedResponse = accountService.getAccountsById(45);

        Assert.assertEquals(returnedResponse.getStatusCode(), HttpStatus.OK);

        List<AccountDTO> returnedAccounts = returnedResponse.getBody();
        Assert.assertEquals(returnedAccounts.size(), 2);

        Integer accountNumbers[] = {4500, 4600};
        AccountType accountTypes[] = {AccountType.CASH, AccountType.CURRENT};
        Double accountBalances[] = {12000.0, 13000.0};
        int i = 0;
        for (AccountDTO returnedAccount : returnedAccounts) {
            Assert.assertEquals(returnedAccount.getAccountId(), new Integer(45));
            Assert.assertEquals(returnedAccount.getAccountNumber(), accountNumbers[i]);
            Assert.assertEquals(returnedAccount.getAccountType(), accountTypes[i]);
            Assert.assertEquals(returnedAccount.getAccountBalance(), accountBalances[i]);
            Assert.assertEquals(returnedAccount.getIsCustomerActive(), Boolean.TRUE);
            i++;
        }
    }

    @Test
    public void testGetAccountsByIdEmpty() throws Exception {
        Mockito.when(accountRepo.findAllByAccountId(70))
                .thenReturn(getEmptyAccounts());

        Assert.assertThrows(AccountNotFoundException.class, () -> {
            accountService.getAccountsById(70);
        });
    }

    private List<Account> getEmptyAccounts() {
        return new ArrayList<Account>();
    }

    private List<Account> getAccountsOfOneCustomer() {
        List<Account> allAccounts = new ArrayList<>();

        Account account1 = new Account();
        account1.setAccountId(45);
        account1.setAccountName("account-test-2");
        account1.setAccountBalance(12000.0);
        account1.setAccountType(AccountType.CASH);
        account1.setIsCustomerActive(Boolean.TRUE);
        account1.setAccountNumber(4500);
        allAccounts.add(account1);

        Account account2 = new Account();
        account2.setAccountId(45);
        account2.setAccountName("account-test-3");
        account2.setAccountBalance(13000.0);
        account2.setAccountType(AccountType.CURRENT);
        account2.setIsCustomerActive(Boolean.TRUE);
        account2.setAccountNumber(4600);
        allAccounts.add(account2);

        return allAccounts;
    }

    private List<Account> getAccountList() {
        List<Account> allAccounts = new ArrayList<>();

        Account account1 = new Account();
        account1.setAccountId(40);
        account1.setAccountName("account-test-1");
        account1.setAccountBalance(8000.0);
        account1.setAccountType(AccountType.CASH);
        account1.setIsCustomerActive(Boolean.TRUE);
        account1.setAccountNumber(4000);
        allAccounts.add(account1);

        Account account2 = new Account();
        account2.setAccountId(45);
        account2.setAccountName("account-test-2");
        account2.setAccountBalance(12000.0);
        account2.setAccountType(AccountType.CASH);
        account2.setIsCustomerActive(Boolean.TRUE);
        account2.setAccountNumber(4500);
        allAccounts.add(account2);

        Account account3 = new Account();
        account3.setAccountId(45);
        account3.setAccountName("account-test-3");
        account3.setAccountBalance(13000.0);
        account3.setAccountType(AccountType.CURRENT);
        account3.setIsCustomerActive(Boolean.TRUE);
        account3.setAccountNumber(4600);
        allAccounts.add(account3);

        Account account4 = new Account();
        account4.setAccountId(50);
        account4.setAccountName("account-test-4");
        account4.setAccountBalance(20000.0);
        account4.setAccountType(AccountType.CASH);
        account4.setIsCustomerActive(Boolean.TRUE);
        account4.setAccountNumber(5000);
        allAccounts.add(account4);

        return allAccounts;
    }

    private Account getDummyAccount() {
        Account account = new Account();
        account.setAccountId(40);
        account.setAccountName("account-test-1");
        account.setAccountBalance(8000.0);
        account.setAccountType(AccountType.CASH);
        account.setIsCustomerActive(Boolean.TRUE);
        account.setAccountNumber(4000);
        return account;
    }
}
