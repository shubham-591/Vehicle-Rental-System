package com.qsp.vehicle_rental_system.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import com.qsp.vehicle_rental_system.entity.Role;
import com.qsp.vehicle_rental_system.entity.Users;
import com.qsp.vehicle_rental_system.exception.UserNotFoundException;
import com.qsp.vehicle_rental_system.repository.UsersRepository;

@ExtendWith(MockitoExtension.class)
public class CurrentUserServiceTest {

    @Mock
    UsersRepository userRepository;

    @InjectMocks
    CurrentUserService currentUserService;

    Users user;
    
    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @BeforeEach
    void setUp() {

        user = new Users();
        user.setUid(1);
        user.setName("Shubham");
        user.setEmail("shubham@gmail.com");
        user.setRole(Role.CUSTOMER);
    }
    
    @Test
    void testGetLoggedInUser_Success() {

        Authentication authentication = mock(Authentication.class);

        when(authentication.getName())
                .thenReturn("shubham@gmail.com");

        SecurityContextHolder.getContext()
                .setAuthentication(authentication);

        when(userRepository.findByEmail("shubham@gmail.com"))
                .thenReturn(Optional.of(user));

        Users response =
                currentUserService.getLoggedInUser();

        assertEquals(1, response.getUid());
        assertEquals("Shubham", response.getName());
        assertEquals("shubham@gmail.com", response.getEmail());

        verify(userRepository)
                .findByEmail("shubham@gmail.com");
    }
    
    @Test
    void testGetLoggedInUser_UserNotFound() {

        Authentication authentication = mock(Authentication.class);

        when(authentication.getName())
                .thenReturn("unknown@gmail.com");

        SecurityContextHolder.getContext()
                .setAuthentication(authentication);

        when(userRepository.findByEmail("unknown@gmail.com"))
                .thenReturn(Optional.empty());

        assertThrows(
                UserNotFoundException.class,
                () -> currentUserService.getLoggedInUser()
        );

        verify(userRepository)
                .findByEmail("unknown@gmail.com");
    }
}
