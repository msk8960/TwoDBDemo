package com.example.jwt.demo.repo;

import com.example.jwt.demo.model.DAOUser;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface UserRepo extends MongoRepository<DAOUser, String> {
    public Optional<DAOUser> findByUsername(String username);
}
