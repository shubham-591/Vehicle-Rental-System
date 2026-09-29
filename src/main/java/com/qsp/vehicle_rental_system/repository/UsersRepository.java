package com.qsp.vehicle_rental_system.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.qsp.vehicle_rental_system.entity.Users;

public interface UsersRepository extends JpaRepository<Users, Integer>{
	
	/*
	 * Now all the inbuilt methods will be availabe to perform the CRUD operations
	 */
	
	// This is a custom method to find user by email
	Optional<Users> findByEmail(String email);
}
