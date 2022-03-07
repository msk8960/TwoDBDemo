package com.example.demo.repo;

import com.example.demo.model.Account;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AccountRepo extends JpaRepository<Account, Integer> {

    List<Account> findAllByAccountId(Integer integer);

    void deleteAllByAccountId(Integer integer);

}
