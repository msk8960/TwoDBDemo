package com.example.jwt.demo.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Data
@Document(collection = "user")
@NoArgsConstructor
public class DAOUser {

    private String username;
    private String encryptedPassword;
    private Set<String> roles;

    public User getSpringUser() {
        List<GrantedAuthority> grantedAuthorityList = roles
                .stream()
                .map((role) -> {
                    return new SimpleGrantedAuthority(role);
                })
                .collect(Collectors.toList());
        return new User(username, encryptedPassword, grantedAuthorityList);
    }

}
