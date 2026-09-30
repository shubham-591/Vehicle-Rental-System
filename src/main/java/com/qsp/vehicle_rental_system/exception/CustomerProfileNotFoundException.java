package com.qsp.vehicle_rental_system.exception;

public class CustomerProfileNotFoundException extends RuntimeException {

    public CustomerProfileNotFoundException(String message) {
        super(message);
    }
}
