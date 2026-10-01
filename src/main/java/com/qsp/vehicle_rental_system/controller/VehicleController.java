package com.qsp.vehicle_rental_system.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.qsp.vehicle_rental_system.dto.VehicleRequest;
import com.qsp.vehicle_rental_system.dto.VehicleResponse;
import com.qsp.vehicle_rental_system.entity.Vehicle;
import com.qsp.vehicle_rental_system.service.VehicleService;

import jakarta.validation.Valid;

@RestController
public class VehicleController {
	
	VehicleService vehicleService;

    @Autowired
    public VehicleController(VehicleService vehicleService) {
        this.vehicleService = vehicleService;
    }

    @PostMapping("/vehicles")
    public ResponseEntity<Vehicle> saveVehicle(
    		@Valid @RequestBody VehicleRequest request) {

        Vehicle vehicle = vehicleService.saveVehicleService(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(vehicle);
    }

    @GetMapping("/vehicles")
    public ResponseEntity<List<VehicleResponse>> getAllVehicles() {

    	 List<VehicleResponse> vehicles =
                vehicleService.getAllVehiclesService();

        return ResponseEntity.ok(vehicles);
    }

    @GetMapping("/vehicles/{id}")
    public ResponseEntity<VehicleResponse> getVehicleById(
            @PathVariable int id) {

    	VehicleResponse vehicle =
                vehicleService.getVehicleByIdService(id);

        return ResponseEntity.ok(vehicle);
    }

    @DeleteMapping("/vehicles/{id}")
    public ResponseEntity<Void> deleteVehicle(
            @PathVariable int id) {

        vehicleService.deleteVehicleService(id);

        return ResponseEntity.noContent().build();
    }
}
