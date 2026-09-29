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
    public ResponseEntity<Rental> rentVehicle(
            @Valid @RequestBody RentalRequest rentalRequest) {

        Rental rental =
                rentalService.rentVehicleService(rentalRequest);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(rental);
    }
    
    @PutMapping("/rentals/{id}/return")
    public ResponseEntity<Rental> returnVehicle(
            @PathVariable int id) {

        Rental rental =
                rentalService.returnVehicleService(id);

        return ResponseEntity.ok(rental);
    }
    
    @GetMapping("/rentals")
    public ResponseEntity<List<Rental>> getAllRentals() {

        List<Rental> rentals =
                rentalService.getAllRentalsService();

        return ResponseEntity.ok(rentals);
    }
    
    @GetMapping("/rentals/{id}")
    public ResponseEntity<Rental> getRentalById(
            @PathVariable int id) {

        Rental rental =
                rentalService.getRentalByIdService(id);

        return ResponseEntity.ok(rental);
    }
}
