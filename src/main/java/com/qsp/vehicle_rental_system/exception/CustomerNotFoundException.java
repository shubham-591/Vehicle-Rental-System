package com.qsp.vehicle_rental_system.exception;

public class CustomerNotFoundException extends RuntimeException{
	
	public CustomerNotFoundException(String message) {
        super(message);
    }
}
