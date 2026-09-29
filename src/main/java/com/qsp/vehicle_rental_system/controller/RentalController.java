package com.qsp.vehicle_rental_system.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.qsp.vehicle_rental_system.dto.RentalRequest;
import com.qsp.vehicle_rental_system.dto.RentalResponse;
import com.qsp.vehicle_rental_system.entity.Rental;
import com.qsp.vehicle_rental_system.service.RentalService;

import jakarta.validation.Valid;

@RestController
public class RentalController {

    RentalService rentalService;

    @Autowired
    public RentalController(RentalService rentalService) {
        this.rentalService = rentalService;
    }

    @PostMapping("/rentals")
    public ResponseEntity<RentalResponse> rentVehicle(
            @Valid @RequestBody RentalRequest rentalRequest) {

    	RentalResponse response =
                rentalService.rentVehicleService(rentalRequest);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }
    
    @PutMapping("/rentals/{id}/return")
    public ResponseEntity<RentalResponse> returnVehicle(
            @PathVariable int id) {

    	RentalResponse response =
                rentalService.returnVehicleService(id);

        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/rentals")
    public ResponseEntity<List<RentalResponse>> getAllRentals() {

    	List<RentalResponse> response =
                rentalService.getAllRentalsService();

        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/rentals/{id}")
    public ResponseEntity<RentalResponse> getRentalById(
            @PathVariable int id) {

    	RentalResponse response =
                rentalService.getRentalByIdService(id);

        return ResponseEntity.ok(response);
    }
}
