package com.qsp.vehicle_rental_system.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.boot.CommandLineRunner;
import com.qsp.vehicle_rental_system.entity.Role;
import com.qsp.vehicle_rental_system.entity.Users;

import com.qsp.vehicle_rental_system.repository.UsersRepository;

@Component
public class AdminDataInitializer implements CommandLineRunner {

	private final UsersRepository usersRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${admin.email}")
    private String adminEmail;

    @Value("${admin.password}")
    private String adminPassword;

    public AdminDataInitializer(
            UsersRepository usersRepository,
            PasswordEncoder passwordEncoder) {

        this.usersRepository = usersRepository;
        this.passwordEncoder = passwordEncoder;
    }
    
    @Override
    public void run(String... args) {
    	
    	if (usersRepository.findByEmail(adminEmail).isPresent()) {
    	    return;
    	}
    	
    	// Create Admin
    	Users admin = new Users();

    	admin.setName("Admin");
    	admin.setEmail(adminEmail);
    	admin.setPassword(passwordEncoder.encode(adminPassword));
    	admin.setRole(Role.ADMIN);

    	usersRepository.save(admin);
    }
}
