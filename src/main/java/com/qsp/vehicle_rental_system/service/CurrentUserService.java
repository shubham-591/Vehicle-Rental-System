package com.qsp.vehicle_rental_system.service;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.qsp.vehicle_rental_system.entity.Users;
import com.qsp.vehicle_rental_system.exception.UserNotFoundException;
import com.qsp.vehicle_rental_system.repository.UsersRepository;

import org.springframework.beans.factory.annotation.Autowired;

@Service
public class CurrentUserService {

    private final UsersRepository userRepository;

    @Autowired
    public CurrentUserService(UsersRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Users getLoggedInUser() {

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        String email = authentication.getName();

        return userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new UserNotFoundException("User not found"));
    }
}
