package com.qsp.vehicle_rental_system.exception;

public class CustomerHasActiveRentalsException extends RuntimeException {

    public CustomerHasActiveRentalsException(String message) {
        super(message);
    }
}
