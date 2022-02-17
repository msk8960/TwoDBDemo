package com.example.demo.controller;

import com.example.demo.exception.CustomerAlreadyExistsException;
import com.example.demo.exception.CustomerNotFoundException;
import com.example.demo.feign.AccountFeign;
import com.example.demo.model.Account;
import com.example.demo.model.Customer;
import com.example.demo.model.CustomerAccountResponse;
import com.example.demo.model.CustomerType;
import com.example.demo.repo.CustomerRepo;
import feign.FeignException;
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
@RequestMapping("/customer")
@RestController
public class CustomerController {

    @Autowired
    CustomerRepo customerRepo;

    @Autowired
    AccountFeign accountFeign;

    @PostMapping("/create")
    public ResponseEntity<Customer> createCustomer(@Valid @RequestBody Customer customer) {
        try {
            log.info("adding customer");

            if (!customerRepo.findByCustomerId(customer.getCustomerId()).isPresent()) {
                boolean accountExists = false;
                try {
                    log.info("calling account service");
                    ResponseEntity response = accountFeign.getAccountById(customer.getCustomerId());

                    if (response.getStatusCode() == HttpStatus.OK) {
                        log.info("account service found an account with id - " + customer.getCustomerId());
                        accountExists = true;
                    }
                } catch (FeignException ex) {

                    log.error(ex.getMessage());
                    if (ex.status() == HttpStatus.NOT_FOUND.value()) {
                        log.info("account service returned account not found");
                        accountExists = false;
                    }
                }

                if(!accountExists) {
                    Customer savedCustomer = customerRepo.save(new Customer(
                            customer.getCustomerId(), customer.getCustomerName(),
                            new Date(), CustomerType.INDIVIDUAL, customer.isActive()));

                    log.info("customer added to the database");
                    return new ResponseEntity<>(savedCustomer, HttpStatus.CREATED);
                } else {
                    log.info("an account with given customer id already exists");
                    throw new CustomerAlreadyExistsException("an account with customer id already exists");
                }
            } else {
                log.info("customer already exists");
                throw new CustomerAlreadyExistsException("customer already exists with given customer id");
            }
        } catch (CustomerAlreadyExistsException e) {
            log.error(e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error(e.getMessage());
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/customers")
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
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/customers/{id}")
    public ResponseEntity<CustomerAccountResponse> getCustomerById(@PathVariable("id") Integer id) {
        try {
            log.info("retrieving customer id - " + id);
            Optional<Customer> selectedCustomer = customerRepo.findByCustomerId(id);

            CustomerAccountResponse car = new CustomerAccountResponse();
            try {
                ResponseEntity<Account> accountResponse = accountFeign.getAccountById(id);
                car.setAccount(accountResponse.getBody());

                if (selectedCustomer.isPresent()) {
                    car.setCustomer(selectedCustomer.get());
                } else {
                    log.error("customer not found for id " + id);
                    throw new CustomerNotFoundException("customer not found for the id " + id);
                }

                log.info("customer id " + id + " retrieved");
                return new ResponseEntity<>(car, HttpStatus.OK);
            } catch (FeignException ex) {

                log.error(ex.getMessage());
                if (ex.status() == HttpStatus.NOT_FOUND.value()) {
                    log.info("account service returned account not found");
                    throw new CustomerNotFoundException("account not found for the customer");
                } else {
                    return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
                }
            }
        } catch (CustomerNotFoundException e) {
            log.error(e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error(e.getMessage());
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}

