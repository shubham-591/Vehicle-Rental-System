package com.qsp.vehicle_rental_system.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.qsp.vehicle_rental_system.entity.Customer;
import com.qsp.vehicle_rental_system.exception.CustomerNotFoundException;
import com.qsp.vehicle_rental_system.repository.CustomerRepository;

@Service
public class CustomerService {
	
	CustomerRepository customerRepository;
	
	
	@Autowired
	public CustomerService(CustomerRepository customerRepository) {
		this.customerRepository = customerRepository;
	}
	
	public Customer saveCustomerService(Customer c) {
		return customerRepository.save(c);	
	}
	
	public List<Customer> getAllCustomersService() {
        return customerRepository.findAll();
    }

    public Customer getCustomerByIdService(int id) {
    	
    	/*
    	 * This is one of the way that we have studied
    	 * i.e, Using Optional Class, we will see other way in delete service
    	 */
    	Optional<Customer> optionalCustomer =
                customerRepository.findById(id);

        if (optionalCustomer.isPresent()) {
            return optionalCustomer.get();
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

        customerRepository.delete(customer);
    }
}
