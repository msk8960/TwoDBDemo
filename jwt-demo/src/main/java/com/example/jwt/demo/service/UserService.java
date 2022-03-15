package com.example.jwt.demo.service;

import com.example.jwt.demo.model.DAOUser;
import com.example.jwt.demo.repo.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService implements UserDetailsService {

    @Autowired
    private UserRepo userRepo;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<DAOUser> daoUser = userRepo.findByUsername(username);

        if (!daoUser.isPresent()) {
            throw new UsernameNotFoundException("username not found - " + username);
        }

        return daoUser.get().getSpringUser();
    }
}
