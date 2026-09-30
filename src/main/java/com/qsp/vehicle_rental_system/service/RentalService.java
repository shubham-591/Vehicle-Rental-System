package com.qsp.vehicle_rental_system.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.qsp.vehicle_rental_system.dto.RentalRequest;
import com.qsp.vehicle_rental_system.dto.RentalResponse;
import com.qsp.vehicle_rental_system.dto.VehicleResponse;
import com.qsp.vehicle_rental_system.entity.Customer;
import com.qsp.vehicle_rental_system.entity.Rental;
import com.qsp.vehicle_rental_system.entity.Role;
import com.qsp.vehicle_rental_system.entity.Users;
import com.qsp.vehicle_rental_system.entity.Vehicle;
import com.qsp.vehicle_rental_system.exception.CustomerNotFoundException;
import com.qsp.vehicle_rental_system.exception.CustomerProfileNotFoundException;
import com.qsp.vehicle_rental_system.exception.RentalAlreadyReturnedException;
import com.qsp.vehicle_rental_system.exception.RentalNotFoundException;
import com.qsp.vehicle_rental_system.exception.UserNotFoundException;
import com.qsp.vehicle_rental_system.exception.VehicleNotAvailableException;
import com.qsp.vehicle_rental_system.exception.VehicleNotFoundException;
import com.qsp.vehicle_rental_system.repository.CustomerRepository;
import com.qsp.vehicle_rental_system.repository.RentalRepository;
import com.qsp.vehicle_rental_system.repository.UsersRepository;
import com.qsp.vehicle_rental_system.repository.VehicleRepository;

@Service
public class RentalService {

    RentalRepository rentalRepository;
    CustomerRepository customerRepository;
    VehicleRepository vehicleRepository;
    UsersRepository userRepository;

    @Autowired
    public RentalService(
            RentalRepository rentalRepository,
            CustomerRepository customerRepository,
            VehicleRepository vehicleRepository,
            UsersRepository userRepository) {

        this.rentalRepository = rentalRepository;
        this.customerRepository = customerRepository;
        this.vehicleRepository = vehicleRepository;
        this.userRepository = userRepository;
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

 
    	Users user = getLoggedInUser();

    	// Get customer's profile
    	Customer customer = user.getCustomer();

    	if (customer == null) {
    	    throw new CustomerProfileNotFoundException("Customer profile not found");
    	}

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
        
        Users user = getLoggedInUser();
        
        // Check rental ownership for CUSTOMER
        if (user.getRole() == Role.CUSTOMER) {

            if (user.getCustomer() == null ||
                rental.getCustomer().getCid() != user.getCustomer().getCid()) {

                throw new AccessDeniedException(
                        "You are not allowed to return this rental"
                );
            }
        }

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

    	Users user = getLoggedInUser();

        List<Rental> rentals;

        if (user.getRole() == Role.ADMIN) {
            // ADMIN → all rentals
            rentals = rentalRepository.findAll();

        } else {
            // CUSTOMER → only their rentals
            rentals = rentalRepository.findByCustomer(user.getCustomer());
        }

        return rentals.stream()
                .map(this::convertToRentalResponse)
                .toList();
    }
    
    public RentalResponse getRentalByIdService(int id) {

        Rental rental = rentalRepository.findById(id)
                .orElseThrow(() ->
                        new RentalNotFoundException(
                                "Rental not found with ID: " + id
                        )
                );

        Users user = getLoggedInUser();

        // CUSTOMER can view only their own rental
        if (user.getRole() == Role.CUSTOMER) {

            if (user.getCustomer() == null ||
                rental.getCustomer().getCid() != user.getCustomer().getCid()) {

                throw new AccessDeniedException(
                        "You are not allowed to view this rental"
                );
            }
        }

        return convertToRentalResponse(rental);
    }
    
    // Helper Method to get logged in user
    private Users getLoggedInUser() {
    	
    	// Get currently logged-in user
//      Authentication authentication =
//             SecurityContextHolder.getContext().getAuthentication();
      
	      // Above one is the short way of writing the below code.
	      SecurityContext context =
	              SecurityContextHolder.getContext();
	
	      Authentication authentication =
	              context.getAuthentication();
	
	      String email = authentication.getName();

	      return userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new UserNotFoundException("User not found"));
    }
}
