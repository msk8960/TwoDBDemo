package com.example.demo.service;

import com.example.demo.exception.CustomerAlreadyExistsException;
import com.example.demo.exception.CustomerNotActiveException;
import com.example.demo.exception.CustomerNotFoundException;
import com.example.demo.feign.AccountFeign;
import com.example.demo.model.*;
import com.example.demo.repo.CustomerRepo;
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

import java.util.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class CustomerServiceImplTest {

    @MockBean
    CustomerRepo customerRepo;

    @MockBean
    AccountFeign accountFeign;

    @Autowired
    @InjectMocks
    CustomerServiceImpl customerService;

    @Test
    public void testCreateCustomer() throws Exception {
        Customer savedCustomer = getDummyCustomer();
        savedCustomer.setCreationDate(new Date());
        Mockito.when(customerRepo.findByCustomerId(40)).thenReturn(Optional.empty());
        Mockito.when(customerRepo.save(Mockito.any(Customer.class))).thenReturn(savedCustomer);

        ResponseEntity<AccountDTO> feignAccountResponse = getDefaultAccountResponse();
        feignAccountResponse.getBody().setCreationDate(savedCustomer.getCreationDate());
        Mockito.when(accountFeign.createAccount(Mockito.any(AccountDTO.class)))
                .thenReturn(feignAccountResponse);

        Customer toCreateCustomer = getDummyCustomer();

        ResponseEntity<CustomerAccountResponse> returnedResponse
                = customerService.createCustomer(toCreateCustomer);
        Assert.assertEquals(returnedResponse.getStatusCode(), HttpStatus.CREATED);
        CustomerAccountResponse returnedCustomerAccountResponse = returnedResponse.getBody();

        CustomerDTO returnedCustomer = returnedCustomerAccountResponse.getCustomer();
        Assert.assertEquals(toCreateCustomer.getCustomerId(), returnedCustomer.getCustomerId());
        Assert.assertEquals(toCreateCustomer.getCustomerName(), returnedCustomer.getCustomerName());
        Assert.assertEquals(toCreateCustomer.getCustomerType(), returnedCustomer.getCustomerType());
        Assert.assertTrue(returnedCustomer.isActive());

        Assert.assertEquals(savedCustomer.getCreationDate(), returnedCustomer.getCreationDate());

        Assert.assertEquals(returnedCustomerAccountResponse.getAccounts().size(), 1);

        AccountDTO returnedAccount = returnedCustomerAccountResponse.getAccounts().iterator().next();
        Assert.assertEquals(returnedAccount.getAccountId(), toCreateCustomer.getCustomerId());
        Assert.assertEquals(returnedAccount.getAccountName(), "customer-test-1-account-cash");
        Assert.assertEquals(returnedAccount.getAccountType(), AccountType.CASH);
        Assert.assertEquals(returnedAccount.getAccountBalance(), new Double(5000.0));
        Assert.assertEquals(returnedAccount.getIsCustomerActive(), Boolean.TRUE);
        Assert.assertEquals(returnedAccount.getCreationDate(), savedCustomer.getCreationDate());
    }

    @Test
    public void testCreateCustomerAlreadyExists() throws Exception {
        Customer savedCustomer = getDummyCustomer();
        savedCustomer.setCreationDate(new Date());
        Optional<Customer> alreadyExistingCustomer = Optional.of(savedCustomer);
        Mockito.when(customerRepo.findByCustomerId(40)).thenReturn(alreadyExistingCustomer);
        Mockito.when(customerRepo.save(Mockito.any(Customer.class))).thenReturn(savedCustomer);

        ResponseEntity<AccountDTO> defaultAccountResponse = getDefaultAccountResponse();
        defaultAccountResponse.getBody().setCreationDate(savedCustomer.getCreationDate());
        Mockito.when(accountFeign.createAccount(Mockito.any(AccountDTO.class)))
                .thenReturn(defaultAccountResponse);

        Assert.assertThrows(CustomerAlreadyExistsException.class, () -> {
            customerService.createCustomer(getDummyCustomer());
        });
    }

    @Test
    public void testCreateCustomerInactive() throws Exception {
        Customer savedCustomer = getDummyCustomer();
        savedCustomer.setCreationDate(new Date());
        Mockito.when(customerRepo.findByCustomerId(40)).thenReturn(Optional.empty());
        Mockito.when(customerRepo.save(Mockito.any(Customer.class))).thenReturn(savedCustomer);

        ResponseEntity<AccountDTO> defaultAccountResponse = getDefaultAccountResponse();
        defaultAccountResponse.getBody().setCreationDate(savedCustomer.getCreationDate());
        Mockito.when(accountFeign.createAccount(Mockito.any(AccountDTO.class)))
                .thenReturn(defaultAccountResponse);

        Customer toCreateCustomer = getDummyCustomer();
        toCreateCustomer.setActive(false);

        Assert.assertThrows(CustomerNotActiveException.class, () -> {
            customerService.createCustomer(toCreateCustomer);
        });
    }

    @Test
    public void testCreateCustomerNull() throws Exception {
        Customer savedCustomer = getDummyCustomer();
        savedCustomer.setCreationDate(new Date());
        Mockito.when(customerRepo.findByCustomerId(40)).thenReturn(Optional.empty());
        Mockito.when(customerRepo.save(Mockito.any(Customer.class))).thenReturn(savedCustomer);

        ResponseEntity<AccountDTO> defaultAccountResponse = getDefaultAccountResponse();
        defaultAccountResponse.getBody().setCreationDate(savedCustomer.getCreationDate());
        Mockito.when(accountFeign.createAccount(Mockito.any(AccountDTO.class)))
                .thenReturn(defaultAccountResponse);

        ResponseEntity<CustomerAccountResponse> returnedResponse
                = customerService.createCustomer(null);

        Assert.assertEquals(returnedResponse.getStatusCode(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Test
    public void testGetAllCustomers() throws Exception {
        List<Customer> repoCustomers = getDummyCustomers();
        Mockito.when(customerRepo.findAll()).thenReturn(repoCustomers);

        ResponseEntity<List<CustomerDTO>> returnedResponse = customerService.getAllCustomers();

        Assert.assertEquals(returnedResponse.getStatusCode(), HttpStatus.OK);
        List<CustomerDTO> returnedCustomers = returnedResponse.getBody();

        Assert.assertEquals(returnedCustomers.size(), 2);

        Iterator<Customer> repoCustomersIterator = repoCustomers.iterator();
        Iterator<CustomerDTO> returnedCustomersIterator = returnedCustomers.iterator();

        Customer repoCustomer1 = repoCustomersIterator.next();
        CustomerDTO returnedCustomer1 = returnedCustomersIterator.next();

        Assert.assertEquals(repoCustomer1.getCustomerId(), returnedCustomer1.getCustomerId());
        Assert.assertEquals(repoCustomer1.getCustomerName(), returnedCustomer1.getCustomerName());
        Assert.assertEquals(repoCustomer1.getCustomerType(), returnedCustomer1.getCustomerType());
        Assert.assertEquals(repoCustomer1.getCreationDate(), returnedCustomer1.getCreationDate());
        Assert.assertEquals(repoCustomer1.isActive(), returnedCustomer1.isActive());

        Customer repoCustomer2 = repoCustomersIterator.next();
        CustomerDTO returnedCustomer2 = returnedCustomersIterator.next();

        Assert.assertEquals(repoCustomer2.getCustomerId(), returnedCustomer2.getCustomerId());
        Assert.assertEquals(repoCustomer2.getCustomerName(), returnedCustomer2.getCustomerName());
        Assert.assertEquals(repoCustomer2.getCustomerType(), returnedCustomer2.getCustomerType());
        Assert.assertEquals(repoCustomer2.getCreationDate(), returnedCustomer2.getCreationDate());
        Assert.assertEquals(repoCustomer2.isActive(), returnedCustomer2.isActive());
    }

    @Test
    public void testGetAllCustomersEmpty() throws Exception {
        List<Customer> emptyCustomers = getEmptyCustomers();
        Mockito.when(customerRepo.findAll()).thenReturn(emptyCustomers);

        ResponseEntity<List<CustomerDTO>> returnedResponse = customerService.getAllCustomers();

        Assert.assertEquals(returnedResponse.getStatusCode(), HttpStatus.NO_CONTENT);
        Assert.assertEquals(returnedResponse.getBody(), null);
    }

    @Test
    public void testGetCustomerById() throws Exception {
        Customer repoCustomer = getDummyCustomer();
        Optional<Customer> repoCustomerVal = Optional.of(repoCustomer);
        Mockito.when(customerRepo.findByCustomerId(40)).thenReturn(repoCustomerVal);

        ResponseEntity<List<AccountDTO>> feignAccountResponse
                = getAccountsForCustomerResponse();
        Mockito.when(accountFeign.getAccountsById(40))
                .thenReturn(feignAccountResponse);

        ResponseEntity<CustomerAccountResponse> returnedResponse
                = customerService.getCustomerById(40);
        Assert.assertEquals(returnedResponse.getStatusCode(), HttpStatus.OK);
        CustomerAccountResponse returnedCustomerAccountResponse = returnedResponse.getBody();

        CustomerDTO returnedCustomer = returnedCustomerAccountResponse.getCustomer();
        Assert.assertEquals(repoCustomer.getCustomerId(), returnedCustomer.getCustomerId());
        Assert.assertEquals(repoCustomer.getCustomerName(), returnedCustomer.getCustomerName());
        Assert.assertEquals(repoCustomer.getCustomerType(), returnedCustomer.getCustomerType());
        Assert.assertEquals(repoCustomer.getCreationDate(), returnedCustomer.getCreationDate());

        List<AccountDTO> returnedAccounts = returnedCustomerAccountResponse.getAccounts();
        Assert.assertEquals(returnedAccounts.size(), 2);
        Iterator<AccountDTO> accountsIterator = returnedCustomerAccountResponse.getAccounts().iterator();

        AccountDTO returnedAccount1 = accountsIterator.next();
        Assert.assertEquals(returnedAccount1.getAccountId(), returnedCustomer.getCustomerId());
        Assert.assertEquals(returnedAccount1.getAccountName(), "customer-test-1-account-cash");
        Assert.assertEquals(returnedAccount1.getAccountType(), AccountType.CASH);
        Assert.assertEquals(returnedAccount1.getAccountBalance(), new Double(14000.0));

        AccountDTO returnedAccount2 = accountsIterator.next();
        Assert.assertEquals(returnedAccount2.getAccountId(), returnedCustomer.getCustomerId());
        Assert.assertEquals(returnedAccount2.getAccountName(), "customer-test-1-account-current");
        Assert.assertEquals(returnedAccount2.getAccountType(), AccountType.CURRENT);
        Assert.assertEquals(returnedAccount2.getAccountBalance(), new Double(21000.0));
    }

    @Test
    public void testGetCustomerByIdNotFound() throws Exception {
        Mockito.when(customerRepo.findByCustomerId(40)).thenReturn(Optional.empty());

        ResponseEntity<List<AccountDTO>> feignAccountResponse
                = getAccountsForCustomerResponse();
        Mockito.when(accountFeign.getAccountsById(40))
                .thenReturn(feignAccountResponse);

        Assert.assertThrows(CustomerNotFoundException.class, () -> {
            customerService.getCustomerById(40);
        });
    }

    private ResponseEntity<List<AccountDTO>> getAccountsForCustomerResponse() {
        List<AccountDTO> accountsOfCustomer = new ArrayList<>();

        accountsOfCustomer.add(new AccountDTO(40, "customer-test-1-account-cash",
                new Date(), AccountType.CASH, Boolean.TRUE, 14000.0));
        accountsOfCustomer.add(new AccountDTO(40, "customer-test-1-account-current",
                new Date(), AccountType.CURRENT, Boolean.TRUE, 21000.0));
        return new ResponseEntity<>(accountsOfCustomer, HttpStatus.OK);

    }

    private ResponseEntity<AccountDTO> getDefaultAccountResponse() {
        return new ResponseEntity<>(new AccountDTO(40, "customer-test-1-account-cash",
                new Date(), AccountType.CASH, Boolean.TRUE, 5000.0),
                HttpStatus.CREATED);

    }

    private List<Customer> getDummyCustomers() {
        List<Customer> customers = new ArrayList<>();
        Customer customer1 = new Customer();
        customer1.setCustomerId(40);
        customer1.setCustomerName("customer-test-1");
        customer1.setCustomerType(CustomerType.INDIVIDUAL);
        customer1.setActive(true);
        customer1.setCreationDate(new Date());
        customers.add(customer1);

        Customer customer2 = new Customer();
        customer2.setCustomerId(45);
        customer2.setCustomerName("customer-test-2");
        customer2.setCustomerType(CustomerType.INDIVIDUAL);
        customer2.setActive(true);
        customer2.setCreationDate(new Date());
        customers.add(customer2);

        return customers;
    }

    private List<Customer> getEmptyCustomers() {
        return new ArrayList<>();
    }

    private Customer getDummyCustomer() {
        Customer customer = new Customer();
        customer.setCustomerId(40);
        customer.setCustomerName("customer-test-1");
        customer.setCustomerType(CustomerType.INDIVIDUAL);
        customer.setActive(true);
        customer.setCreationDate(new Date());
        return customer;
    }
}
