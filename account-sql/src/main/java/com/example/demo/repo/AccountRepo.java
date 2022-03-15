package com.example.demo.repo;

import com.example.demo.model.Account;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AccountRepo extends JpaRepository<Account, Integer> {

    public List<Account> findAllByAccountIdOrderByAccountNumberAsc(Integer integer);

    public void deleteByAccountId(Integer integer);

}
