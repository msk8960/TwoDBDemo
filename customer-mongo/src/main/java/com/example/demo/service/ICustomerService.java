package com.example.demo.service;

import com.example.demo.model.Customer;
import com.example.demo.model.CustomerAccountResponse;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface ICustomerService {
    public ResponseEntity<CustomerAccountResponse> createCustomer(Customer customer);

    public ResponseEntity<List<Customer>> getAllCustomers();

    public ResponseEntity<CustomerAccountResponse> getCustomerById(Integer id);
}
