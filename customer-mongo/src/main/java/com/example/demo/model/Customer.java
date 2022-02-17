package com.example.demo.model;


import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotBlank;
import java.util.Date;

@Document(collection="customer")
@Data
public class Customer {

    @Id
    Integer customerId;
    @NotBlank(message="name cannot be empty")
    String customerName;
    Date creationDate;
    CustomerType customerType;
    boolean isActive;

    public Customer(Integer customerId, String customerName,
                    Date creationDate, CustomerType customerType, boolean isActive) {
        this.customerId = customerId;
        this.customerName = customerName;
        this.creationDate = creationDate;
        this.customerType = customerType;
        this.isActive = isActive;
    }

    public Customer()
    {

    }

}
