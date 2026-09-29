package com.qsp.vehicle_rental_system.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.qsp.vehicle_rental_system.entity.Vehicle;

public interface VehicleRepository extends JpaRepository<Vehicle, Integer>{
	
	/*
	 * Now all the inbuilt methods will be availabe to perform the CRUD operations
	 */
}
