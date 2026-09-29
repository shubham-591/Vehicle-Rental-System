package com.qsp.vehicle_rental_system.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.qsp.vehicle_rental_system.dto.LoginRequest;
import com.qsp.vehicle_rental_system.dto.RegisterRequest;
import com.qsp.vehicle_rental_system.dto.UserResponse;
import com.qsp.vehicle_rental_system.entity.Users;
import com.qsp.vehicle_rental_system.service.UsersService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/auth")
public class AuthController {

    UsersService userService;

    public AuthController(UsersService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<UserResponse> registerUser(
            @Valid @RequestBody RegisterRequest request) {

    	UserResponse response = userService.registerUserService(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }
    
    @PostMapping("/login")
    public ResponseEntity<String> loginUser(
            @Valid @RequestBody LoginRequest request) {

//        Users user = userService.loginUser(request);
    	String token = userService.loginUser(request);

        return ResponseEntity.ok(token);

//        return ResponseEntity.ok(user);
    }
}
