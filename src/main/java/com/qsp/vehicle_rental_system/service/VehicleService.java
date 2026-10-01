package com.qsp.vehicle_rental_system.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.qsp.vehicle_rental_system.dto.VehicleRequest;
import com.qsp.vehicle_rental_system.dto.VehicleResponse;
import com.qsp.vehicle_rental_system.entity.Role;
import com.qsp.vehicle_rental_system.entity.Users;
import com.qsp.vehicle_rental_system.entity.Vehicle;
import com.qsp.vehicle_rental_system.exception.VehicleNotAvailableException;
import com.qsp.vehicle_rental_system.exception.VehicleNotFoundException;
import com.qsp.vehicle_rental_system.repository.VehicleRepository;

@Service
public class VehicleService {
	
	VehicleRepository vehicleRepository;
	CurrentUserService currentUserService;

    @Autowired
    public VehicleService(VehicleRepository vehicleRepository, CurrentUserService currentUserService) {
        this.vehicleRepository = vehicleRepository;
        this.currentUserService = currentUserService;
    }

    public Vehicle saveVehicleService(VehicleRequest request) {

        Vehicle vehicle = new Vehicle();

        vehicle.setVname(request.getVname());
        vehicle.setRentPerDay(request.getRentPerDay());
        vehicle.setVehicleNumber(request.getVehicleNumber());
        vehicle.setCompany(request.getCompany());

        vehicle.setActive(true);
        vehicle.setAvailable(true);

        return vehicleRepository.save(vehicle);
    }

    public List<VehicleResponse> getAllVehiclesService() {
    	
    	Users user = currentUserService.getLoggedInUser();

    	List<Vehicle> vehicles;

        if (user.getRole() == Role.ADMIN) {
            vehicles = vehicleRepository.findAll();
        } else {
            vehicles = vehicleRepository.findByActiveTrue();
        }

        return vehicles.stream()
                .map(this::convertToVehicleResponse)
                .toList();
    }

    public VehicleResponse getVehicleByIdService(int id) {

        Optional<Vehicle> optionalVehicle =
                vehicleRepository.findById(id);
        
        Users user = currentUserService.getLoggedInUser();

        if (optionalVehicle.isPresent()) {
        	Vehicle vehicle = optionalVehicle.get();

            if (user.getRole() == Role.CUSTOMER && !vehicle.isActive()) {
                throw new VehicleNotFoundException(
                        "Vehicle not found with ID: " + id
                );
            }

            return convertToVehicleResponse(vehicle);
        }

        throw new VehicleNotFoundException(
                "Vehicle not found with ID: " + id
        );
    }

    // Retire this vehicle from the system, rather than physically deleting its database row.
    public void deleteVehicleService(int id) {

        Vehicle vehicle = vehicleRepository.findById(id)
                .orElseThrow(() ->
                    new VehicleNotFoundException(
                        "Vehicle not found with ID: " + id
                    )
                );
        
        if (!vehicle.isAvailable()) {
            throw new VehicleNotAvailableException(
                "Vehicle cannot be retired because it is currently rented"
            );
        }

        vehicle.setActive(false);

        vehicleRepository.save(vehicle);
    }
    
    private VehicleResponse convertToVehicleResponse(Vehicle vehicle) {

        VehicleResponse response = new VehicleResponse();

        response.setVid(vehicle.getVid());
        response.setVname(vehicle.getVname());
        response.setRentPerDay(vehicle.getRentPerDay());
        response.setVehicleNumber(vehicle.getVehicleNumber());
        response.setCompany(vehicle.getCompany());
        response.setAvailable(vehicle.isAvailable());
        response.setActive(vehicle.isActive());

        return response;
    }
}
