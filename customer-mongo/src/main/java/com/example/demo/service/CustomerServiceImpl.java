package com.example.demo.service;

import com.example.demo.exception.CustomerAlreadyExistsException;
import com.example.demo.exception.CustomerNotActiveException;
import com.example.demo.exception.CustomerNotFoundException;
import com.example.demo.feign.AccountFeign;
import com.example.demo.model.*;
import com.example.demo.repo.CustomerRepo;
import feign.FeignException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.*;

@Slf4j
@Service
public class CustomerServiceImpl implements ICustomerService {

    @Autowired
    CustomerRepo customerRepo;

    @Autowired
    AccountFeign accountFeign;

    @Override
    public ResponseEntity<CustomerAccountResponse> createCustomer(Customer customer) {
        try {
            log.info("adding customer");

            if (!customerRepo.findByCustomerId(customer.getCustomerId()).isPresent()) {

                if (!customer.isActive()) {
                    log.error("cannot create customer that is not active");
                    throw new CustomerNotActiveException("cannot create customer that is not active");
                }
                CustomerAccountResponse customerAccountResponse = new CustomerAccountResponse();
                Customer savedCustomer = customerRepo.save(new Customer(
                        customer.getCustomerId(), customer.getCustomerName(),
                        new Date(), CustomerType.INDIVIDUAL, customer.isActive()));
                log.info("customer added to database");
                customerAccountResponse.setCustomer(savedCustomer);

                customerAccountResponse.setAccounts(Arrays.asList(createAccountForCustomer(customer)));

                log.info("customer and account added to the database");
                return new ResponseEntity<>(customerAccountResponse, HttpStatus.CREATED);
            } else {
                log.info("customer already exists");
                throw new CustomerAlreadyExistsException("customer already exists with given customer id");
            }
        } catch (CustomerAlreadyExistsException | CustomerNotActiveException e) {
            log.error(e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Override
    public ResponseEntity<List<Customer>> getAllCustomers() {
        try {
            log.info("retrieving list of customers");
            List<Customer> customers = new ArrayList<>();
            customerRepo.findAll().forEach(customers::add);

            if (customers.isEmpty()) {
                log.info("list of customers is empty");
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }

            log.info("list of customers retrieved");
            return new ResponseEntity<>(customers, HttpStatus.OK);
        } catch (Exception e) {
            log.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Override
    public ResponseEntity<CustomerAccountResponse> getCustomerById(Integer id) {
        try {
            log.info("retrieving customer id - " + id);
            Optional<Customer> selectedCustomer = customerRepo.findByCustomerId(id);

            CustomerAccountResponse car = new CustomerAccountResponse();

            if (selectedCustomer.isPresent()) {
                car.setCustomer(selectedCustomer.get());
            } else {
                log.error("customer not found for id " + id);
                throw new CustomerNotFoundException("customer not found for the id " + id);
            }

            car.setAccounts(getAccountsByCustomerId(id));

            log.info("customer id " + id + " retrieved");
            return new ResponseEntity<>(car, HttpStatus.OK);
        } catch (CustomerNotFoundException e) {
            log.error(e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    private Account createAccountForCustomer(Customer customer) {
        try {
            log.info("calling account service for account creation");
            Account newAccount = accountFeign.createAccount(
                    new Account(customer.getCustomerId(),
                            customer.getCustomerName() + "-account-cash",
                            new Date(), AccountType.CASH, Boolean.TRUE, 5000.0)).getBody();

            log.info("a cash account created for customer in the database");
            return newAccount;
        } catch (Exception e) {
            log.error("error creating account for customer " + customer.getCustomerId());
            log.error(e.getMessage());
            throw e;
        }
    }

    private List<Account> getAccountsByCustomerId(Integer id) {
        try {
            log.info("retrieving accounts for customer with customer id - " + id);
            return accountFeign.getAccountsById(id).getBody();
        } catch (FeignException ex) {
            log.error(ex.getMessage());
            if (ex.status() == HttpStatus.NOT_FOUND.value()) {
                log.info("account service returned account not found");
                throw new CustomerNotFoundException("account not found for the customer");
            }
            throw ex;
        }
    }
}
