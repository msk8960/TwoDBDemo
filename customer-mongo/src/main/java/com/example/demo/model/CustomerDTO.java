package com.example.demo.model;


import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Date;

@NoArgsConstructor
@Data
public class CustomerDTO {

    @NotNull(message = "customer id cannot be null")
    private Integer customerId;

    @NotBlank(message = "customer name cannot be empty")
    private String customerName;

    private Date creationDate;

    @NotNull(message = "customer type cannot be null")
    private CustomerType customerType;

    private boolean isActive;

    public CustomerDTO(Customer customer) {
        this.customerId = customer.getCustomerId();
        this.customerName = customer.getCustomerName();
        this.creationDate = customer.getCreationDate();
        this.customerType = customer.getCustomerType();
        this.isActive = customer.isActive();
    }
}
