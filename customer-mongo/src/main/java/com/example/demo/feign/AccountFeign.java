package com.example.demo.feign;

import com.example.demo.model.Account;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name="account-sql", fallbackFactory=HystrixFallBackFactory.class)
public interface AccountFeign {

    @GetMapping(value = "/account/accounts/{id}")
    ResponseEntity<Account> getAccountById(@PathVariable("id") Integer id);

}

