package com.qsp.vehicle_rental_system.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.qsp.vehicle_rental_system.dto.RentalRequest;
import com.qsp.vehicle_rental_system.dto.RentalResponse;
import com.qsp.vehicle_rental_system.dto.VehicleResponse;
import com.qsp.vehicle_rental_system.entity.Customer;
import com.qsp.vehicle_rental_system.entity.Rental;
import com.qsp.vehicle_rental_system.entity.Vehicle;
import com.qsp.vehicle_rental_system.exception.CustomerNotFoundException;
import com.qsp.vehicle_rental_system.exception.RentalAlreadyReturnedException;
import com.qsp.vehicle_rental_system.exception.RentalNotFoundException;
import com.qsp.vehicle_rental_system.exception.VehicleNotAvailableException;
import com.qsp.vehicle_rental_system.exception.VehicleNotFoundException;
import com.qsp.vehicle_rental_system.repository.CustomerRepository;
import com.qsp.vehicle_rental_system.repository.RentalRepository;
import com.qsp.vehicle_rental_system.repository.VehicleRepository;

@Service
public class RentalService {

    RentalRepository rentalRepository;
    CustomerRepository customerRepository;
    VehicleRepository vehicleRepository;

    @Autowired
    public RentalService(
            RentalRepository rentalRepository,
            CustomerRepository customerRepository,
            VehicleRepository vehicleRepository) {

        this.rentalRepository = rentalRepository;
        this.customerRepository = customerRepository;
        this.vehicleRepository = vehicleRepository;
    }
    
    // Helper Method
    private RentalResponse convertToRentalResponse(Rental rental) {

        RentalResponse response = new RentalResponse();

        response.setRid(rental.getRid());
        response.setStartDate(rental.getStartDate());
        response.setEndDate(rental.getEndDate());
        response.setTotalAmount(rental.getTotalAmount());

        VehicleResponse vehicleResponse = new VehicleResponse();

        vehicleResponse.setVid(rental.getVehicle().getVid());
        vehicleResponse.setVname(rental.getVehicle().getVname());
        vehicleResponse.setVehicleNumber(rental.getVehicle().getVehicleNumber());

        response.setVehicle(vehicleResponse);

        return response;
    }
    
    @Transactional
    public RentalResponse  rentVehicleService(RentalRequest rentalRequest) {
    	
    	if (rentalRequest.getStartDate().isBefore(LocalDate.now())) {
    	    throw new IllegalArgumentException("Rental start date cannot be in the past.");
    	}

        // 1. Find Customer
        Customer customer = customerRepository
                .findById(rentalRequest.getCustomerId())
                .orElseThrow(() ->
                    new CustomerNotFoundException(
                        "Customer not found with ID: "
                        + rentalRequest.getCustomerId()
                    )
                );

        // 2. Find Vehicle
        Vehicle vehicle = vehicleRepository
                .findById(rentalRequest.getVehicleId())
                .orElseThrow(() ->
                    new VehicleNotFoundException(
                        "Vehicle not found with ID: "
                        + rentalRequest.getVehicleId()
                    )
                );

        // 3. Check vehicle availability
        if (!vehicle.isAvailable()) {
            throw new VehicleNotAvailableException(
                "Vehicle with ID "
                + vehicle.getVid()
                + " is already rented"
            );
        }

        // 4. Create Rental object
        Rental rental = new Rental();

        rental.setCustomer(customer);
        rental.setVehicle(vehicle);
        rental.setStartDate(rentalRequest.getStartDate());

        // 5. Vehicle is now rented
        vehicle.setAvailable(false);

        // 6. Save vehicle status
        vehicleRepository.save(vehicle);

        // 7. Save rental
        Rental newRental = rentalRepository.save(rental);

        return convertToRentalResponse(newRental);
    }
    
    @Transactional
    public RentalResponse returnVehicleService(int rentalId) {

        // Find rental
        Rental rental = rentalRepository.findById(rentalId)
                .orElseThrow(() ->
                    new RentalNotFoundException(
                        "Rental not found with ID: " + rentalId
                    )
                );

        // Check whether already returned
        if (rental.getEndDate() != null) {
            throw new RentalAlreadyReturnedException(
                "Rental with ID " + rentalId + " has already been returned"
            );
        }

        // Set return date
        LocalDate endDate = LocalDate.now();
        rental.setEndDate(endDate);

        // Calculate rental days
        long rentalDays = Math.max(
            1,
            java.time.temporal.ChronoUnit.DAYS.between(
                rental.getStartDate(),
                endDate
            )
        );

        // Calculate total amount
        double rentPerDay = rental.getVehicle().getRentPerDay();

        double totalAmount = rentalDays * rentPerDay;

        rental.setTotalAmount(totalAmount);

        // Make vehicle available again
        Vehicle vehicle = rental.getVehicle();
        vehicle.setAvailable(true);

        // Save vehicle
        vehicleRepository.save(vehicle);

        // Save rental
        rentalRepository.save(rental);

        return convertToRentalResponse(rental);
    }
    
    public List<RentalResponse> getAllRentalsService() {

        List<Rental> rentals = rentalRepository.findAll();

        return rentals.stream()
                .map(this::convertToRentalResponse)
                .toList();
    }
    
    public RentalResponse getRentalByIdService(int id) {

        Optional<Rental> optionalRental =
                rentalRepository.findById(id);

        if (optionalRental.isPresent()) {
        	Rental rental = optionalRental.get();

            return convertToRentalResponse(rental);
        }

        throw new RentalNotFoundException(
                "Rental not found with ID: " + id
        );
    }
}
