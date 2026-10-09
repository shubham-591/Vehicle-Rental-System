package com.qsp.vehicle_rental_system.controller;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

import com.qsp.vehicle_rental_system.dto.LoginRequest;
import com.qsp.vehicle_rental_system.dto.RegisterRequest;
import com.qsp.vehicle_rental_system.dto.RentalRequest;
import com.qsp.vehicle_rental_system.dto.RentalResponse;
import com.qsp.vehicle_rental_system.dto.UserResponse;
import com.qsp.vehicle_rental_system.service.RentalService;
import com.qsp.vehicle_rental_system.service.UsersService;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    UsersService userService;

    @InjectMocks
    AuthController authController;

    MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = standaloneSetup(authController).build();
    }

    @Test
    void testRegisterUser() throws Exception {

        RegisterRequest request = new RegisterRequest();
        request.setName("Shubham");
        request.setEmail("shubh@gmail.com");
        request.setPassword("password123");
        request.setMobileNo("9876543210");

        UserResponse response = new UserResponse();
        response.setCustomerId(1);
        response.setName("Shubham");
        response.setEmail("shubh@gmail.com");

        when(userService.registerUserService(any(RegisterRequest.class)))
                .thenReturn(response);

        mockMvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                            "name": "Shubham",
                            "email": "shubh@gmail.com",
                            "password": "password123",
                            "mobileNo": "9876543210"
                        }
                        """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.customerId").value(1))
                .andExpect(jsonPath("$.name").value("Shubham"))
                .andExpect(jsonPath("$.email").value("shubh@gmail.com"));

        verify(userService).registerUserService(any(RegisterRequest.class));
    }
    
    @Test
    void testLoginUser() throws Exception {

        LoginRequest request = new LoginRequest();
        request.setEmail("shubh@gmail.com");
        request.setPassword("password123");

        when(userService.loginUser(any(LoginRequest.class)))
                .thenReturn("dummy-jwt-token");

        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                            "email": "shubh@gmail.com",
                            "password": "password123"
                        }
                        """))
                .andExpect(status().isOk())
                .andExpect(content().string("dummy-jwt-token"));

        verify(userService).loginUser(any(LoginRequest.class));
    }

}
