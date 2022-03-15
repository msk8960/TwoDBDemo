package com.example.demo.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Set;

@Data
@Document(collection = "user")
@NoArgsConstructor
public class DAOUser {

    private String username;
    private String encryptedPassword;
    private Set<String> roles;
}
