package com.qsp.vehicle_rental_system.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.qsp.vehicle_rental_system.dto.CustomerRequest;
import com.qsp.vehicle_rental_system.dto.CustomerResponse;
import com.qsp.vehicle_rental_system.service.CustomerService;

import jakarta.validation.Valid;

@RestController
public class CustomerController {
	
	CustomerService customerService;
	
	@Autowired
	public CustomerController(CustomerService customerService) {
		this.customerService = customerService;
	}

//	Not needed 
//	@PostMapping("/customers")
//	public ResponseEntity<Customer> saveCustomer(@Valid @RequestBody Customer c) {
//		Customer cust = customerService.saveCustomerService(c);
//		return ResponseEntity.status(HttpStatus.CREATED).body(cust);
//	}
	
	@GetMapping("/customers")
	public ResponseEntity<List<CustomerResponse>> getAllCustomers() {

	    List<CustomerResponse> customers =
	            customerService.getAllCustomersService();

	    return ResponseEntity.ok(customers);
	}

    @GetMapping("/customers/{id}")
    public ResponseEntity<CustomerResponse> getCustomerById(
            @PathVariable int id) {

        CustomerResponse customer =
                customerService.getCustomerByIdService(id);

        return ResponseEntity.ok(customer);
    }

    @DeleteMapping("/customers/{id}")
    public ResponseEntity<Void> deleteCustomer(@PathVariable int id) {
        customerService.deleteCustomerService(id);
        return ResponseEntity.noContent().build();
    }
    
    @PatchMapping("/customers/{id}")
    public ResponseEntity<CustomerResponse> updateCustomer(@PathVariable int id, 
    		@Valid @RequestBody CustomerRequest request) {
    	
    	CustomerResponse updatedCustomer = customerService.updateCustomerService(id, request);
    	
    	return ResponseEntity.ok(updatedCustomer);
    }
}
