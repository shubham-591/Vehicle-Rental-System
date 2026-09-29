package com.qsp.vehicle_rental_system.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.qsp.vehicle_rental_system.entity.Vehicle;
import com.qsp.vehicle_rental_system.exception.VehicleNotFoundException;
import com.qsp.vehicle_rental_system.repository.VehicleRepository;

@Service
public class VehicleService {
	
	VehicleRepository vehicleRepository;

    @Autowired
    public VehicleService(VehicleRepository vehicleRepository) {
        this.vehicleRepository = vehicleRepository;
    }

    public Vehicle saveVehicleService(Vehicle v) {
        return vehicleRepository.save(v);
    }

    public List<Vehicle> getAllVehiclesService() {
        return vehicleRepository.findAll();
    }

    public Vehicle getVehicleByIdService(int id) {

        Optional<Vehicle> optionalVehicle =
                vehicleRepository.findById(id);

        if (optionalVehicle.isPresent()) {
            return optionalVehicle.get();
        }

        throw new VehicleNotFoundException(
                "Vehicle not found with ID: " + id
        );
    }

    public void deleteVehicleService(int id) {

        Vehicle vehicle = vehicleRepository.findById(id)
                .orElseThrow(() ->
                    new VehicleNotFoundException(
                        "Vehicle not found with ID: " + id
                    )
                );

        vehicleRepository.delete(vehicle);
    }
}
