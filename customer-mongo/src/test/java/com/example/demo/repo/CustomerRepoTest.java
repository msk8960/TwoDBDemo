package com.example.demo.repo;

import com.example.demo.model.Customer;
import com.example.demo.model.CustomerType;
import org.junit.Assert;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
@DataMongoTest
public class CustomerRepoTest {

    @Autowired
    private CustomerRepo customerRepo;

    @BeforeEach
    private void initRepo() {
        customerRepo.saveAll(createCustomers());
    }

    @AfterEach
    private void deleteRepo() {
        customerRepo.deleteByCustomerId(40);
        customerRepo.deleteByCustomerId(50);
    }

    @Test
    void testFindByCustomerId() {
        Optional<Customer> customerVal = customerRepo.findByCustomerId(50);

        Assert.assertTrue(customerVal.isPresent());
        Customer customer = customerVal.get();
        Assert.assertEquals(customer.getCustomerId(), new Integer(50));
        Assert.assertEquals(customer.getCustomerName(), "customer-test-2");
    }

    @Test
    void testFindByCustomerIdEmpty() {
        Optional<Customer> customerVal = customerRepo.findByCustomerId(600);

        Assert.assertFalse(customerVal.isPresent());
    }

    private List<Customer> createCustomers() {
        List<Customer> customers = new ArrayList<>();

        Customer customer1 = new Customer();
        customer1.setCustomerId(40);
        customer1.setCustomerName("customer-test-1");
        customer1.setCustomerType(CustomerType.INDIVIDUAL);
        customer1.setActive(true);
        customer1.setCreationDate(new Date());
        customers.add(customer1);

        Customer customer2 = new Customer();
        customer2.setCustomerId(50);
        customer2.setCustomerName("customer-test-2");
        customer2.setCustomerType(CustomerType.INDIVIDUAL);
        customer2.setActive(true);
        customer2.setCreationDate(new Date());
        customers.add(customer2);

        return customers;
    }
}

