package com.qsp.vehicle_rental_system.service;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.qsp.vehicle_rental_system.dto.LoginRequest;
import com.qsp.vehicle_rental_system.dto.RegisterRequest;
import com.qsp.vehicle_rental_system.dto.UserResponse;
import com.qsp.vehicle_rental_system.entity.Role;
import com.qsp.vehicle_rental_system.entity.Users;
import com.qsp.vehicle_rental_system.entity.Customer;
import com.qsp.vehicle_rental_system.repository.CustomerRepository;
import com.qsp.vehicle_rental_system.repository.UsersRepository;

@Service
public class UsersService {

    UsersRepository userRepository;
    
    private final JwtService jwtService;
    
    CustomerRepository customerRepository;

    BCryptPasswordEncoder passwordEncoder =
            new BCryptPasswordEncoder();

    public UsersService(UsersRepository userRepository, CustomerRepository customerRepository, JwtService jwtService) {
        this.userRepository = userRepository;
        this.customerRepository = customerRepository;
        this.jwtService = jwtService;
    }

    @Transactional
    public UserResponse  registerUserService(RegisterRequest request) {

    	// Create User
        Users user = new Users();

        user.setName(request.getName());
        user.setEmail(request.getEmail());

        // Convert plain password into a BCrypt hash
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        // Every newly registered user will be a CUSTOMER
        user.setRole(Role.CUSTOMER);
        
        // Create Customer
        Customer customer = new Customer();
        customer.setCname(request.getName());
        customer.setEmail(request.getEmail());
        customer.setMobileNo(request.getMobileNo());

        // Connect both
        user.setCustomer(customer);
        customer.setUser(user);

        // Save Customer
        customer = customerRepository.save(customer);

        // Save User
        user = userRepository.save(user);
        
        UserResponse response = new UserResponse();

        response.setUid(user.getUid());
        response.setName(user.getName());
        response.setEmail(user.getEmail());
        response.setRole(user.getRole());
        response.setCustomerId(customer.getCid());

        return response;
    }
    
    public String loginUser(LoginRequest request) {

        Users user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Invalid email or password"));

        if (!passwordEncoder.matches(
                request.getPassword(),
                user.getPassword())) {

            throw new RuntimeException("Invalid email or password");
        }

        return jwtService.generateToken(user);
    }
}