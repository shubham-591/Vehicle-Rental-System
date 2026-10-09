package com.qsp.vehicle_rental_system.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.qsp.vehicle_rental_system.entity.Customer;
import com.qsp.vehicle_rental_system.entity.Rental;
import com.qsp.vehicle_rental_system.entity.Role;
import com.qsp.vehicle_rental_system.entity.Users;
import com.qsp.vehicle_rental_system.exception.CustomerHasActiveRentalsException;
import com.qsp.vehicle_rental_system.exception.CustomerNotFoundException;
import com.qsp.vehicle_rental_system.repository.CustomerRepository;
import com.qsp.vehicle_rental_system.repository.RentalRepository;
import com.qsp.vehicle_rental_system.dto.CustomerRequest;
import com.qsp.vehicle_rental_system.dto.CustomerResponse;

@Service
public class CustomerService {
	
	CustomerRepository customerRepository;
	RentalRepository rentalRepository;
	CurrentUserService currentUserService;
	
	@Autowired
	public CustomerService(CustomerRepository customerRepository, RentalRepository rentalRepository,
			CurrentUserService currentUserService) {
		this.customerRepository = customerRepository;
		this.rentalRepository = rentalRepository;
		this.currentUserService = currentUserService;
	}
	
	public Customer saveCustomerService(Customer c) {
		return customerRepository.save(c);	
	}
	
	public List<CustomerResponse> getAllCustomersService() {

	    List<Customer> customers = customerRepository.findAll();

	    return customers.stream()
	            .map(this::convertToCustomerResponse)
	            .toList();
	}

    public CustomerResponse getCustomerByIdService(int id) {
    	
    	/*
    	 * This is one of the way that we have studied
    	 * i.e, Using Optional Class, we will see other way in delete service
    	 */
    	Optional<Customer> optionalCustomer =
                customerRepository.findById(id);

        if (optionalCustomer.isPresent()) {
            return convertToCustomerResponse(optionalCustomer.get());
        }

        throw new CustomerNotFoundException(
                "Customer not found with ID: " + id
        );

    }

    public void deleteCustomerService(int id) {
    	Customer customer = customerRepository.findById(id)
                .orElseThrow(() ->
                    new CustomerNotFoundException(
                        "Customer not found with ID: " + id
                    )
                );

    	List<Rental> rentals = rentalRepository.findByCustomer(customer);
    	
    	boolean hasActiveRental = rentals.stream()
                .anyMatch(rental -> rental.getEndDate() == null);

        if (hasActiveRental) {
            throw new CustomerHasActiveRentalsException(
                "Customer cannot be deactivated because they have active rentals"
            );
        }
    	
    	customer.setActive(false);
    	customerRepository.save(customer);
    }
    
    @Transactional
    public CustomerResponse updateCustomerService(int id, CustomerRequest request) {

        Customer customer = customerRepository.findById(id)
                .orElseThrow(() ->
                    new CustomerNotFoundException(
                        "Customer not found with ID: " + id
                    )
                );

        Users loggedInUser = currentUserService.getLoggedInUser();

        // CUSTOMER can update only their own profile
        if (loggedInUser.getRole() == Role.CUSTOMER) {

            if (loggedInUser.getCustomer() == null ||
                loggedInUser.getCustomer().getCid() != customer.getCid()) {

                throw new AccessDeniedException(
                    "You are not allowed to update this customer"
                );
            }
        }

        // Update only provided fields
        if (request.getCname() != null) {
            customer.setCname(request.getCname());
        }

        if (request.getMobileNo() != null) {
            customer.setMobileNo(request.getMobileNo());
        }

        if (request.getEmail() != null) {
            customer.setEmail(request.getEmail());
        }

        // Update linked User
        Users user = customer.getUser();

        if (user != null) {

            if (request.getCname() != null) {
                user.setName(request.getCname());
            }

            if (request.getEmail() != null) {
                user.setEmail(request.getEmail());
            }
        }

        Customer updatedCustomer = customerRepository.save(customer);

        return convertToCustomerResponse(updatedCustomer);
    }
    
    private CustomerResponse convertToCustomerResponse(Customer customer) {

        CustomerResponse response = new CustomerResponse();

        response.setCid(customer.getCid());
        response.setCname(customer.getCname());
        response.setEmail(customer.getEmail());
        response.setMobileNo(customer.getMobileNo());

        return response;
    }
}
