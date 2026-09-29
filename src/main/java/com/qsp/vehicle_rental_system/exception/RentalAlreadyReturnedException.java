package com.qsp.vehicle_rental_system.exception;

public class RentalAlreadyReturnedException extends RuntimeException {

    public RentalAlreadyReturnedException(String message) {
        super(message);
    }

}
