package com.qsp.vehicle_rental_system.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.qsp.vehicle_rental_system.entity.Customer;
import com.qsp.vehicle_rental_system.entity.Rental;

public interface RentalRepository extends JpaRepository<Rental, Integer>{
	
	/*
	 * Now all the inbuilt methods will be availabe to perform the CRUD operations
	 */
	
	List<Rental> findByCustomer(Customer customer);
}
