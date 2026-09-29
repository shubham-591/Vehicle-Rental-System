package com.qsp.vehicle_rental_system.exception;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
	
	@ExceptionHandler(CustomerNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleCustomerNotFound(
            CustomerNotFoundException exception) {

        Map<String, Object> response = new HashMap<>();

        response.put("timestamp", LocalDateTime.now());
        response.put("status", 404);
        response.put("error", "Customer Not Found");
        response.put("message", exception.getMessage());

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(response);
    }
	
	@ExceptionHandler(VehicleNotFoundException.class)
	public ResponseEntity<Map<String, Object>> handleVehicleNotFound(
	        VehicleNotFoundException exception) {

	    Map<String, Object> response = new HashMap<>();

	    response.put("timestamp", LocalDateTime.now());
	    response.put("status", 404);
	    response.put("error", "Vehicle Not Found");
	    response.put("message", exception.getMessage());

	    return ResponseEntity
	            .status(HttpStatus.NOT_FOUND)
	            .body(response);
	}
	
	@ExceptionHandler(RentalNotFoundException.class)
	public ResponseEntity<Map<String, Object>> handleRentalNotFound(
	        RentalNotFoundException exception) {

	    Map<String, Object> response = new HashMap<>();

	    response.put("timestamp", LocalDateTime.now());
	    response.put("status", 404);
	    response.put("error", "Rental Not Found");
	    response.put("message", exception.getMessage());

	    return ResponseEntity
	            .status(HttpStatus.NOT_FOUND)
	            .body(response);
	}
	
	@ExceptionHandler(VehicleNotAvailableException.class)
	public ResponseEntity<Map<String, Object>> handleVehicleNotAvailable(
	        VehicleNotAvailableException exception) {

	    Map<String, Object> response = new HashMap<>();

	    response.put("timestamp", LocalDateTime.now());
	    response.put("status", 409);
	    response.put("error", "Vehicle Not Available");
	    response.put("message", exception.getMessage());

	    return ResponseEntity
	            .status(HttpStatus.CONFLICT)
	            .body(response);
	}
	
	@ExceptionHandler(RentalAlreadyReturnedException.class)
	public ResponseEntity<Map<String, Object>> handleRentalAlreadyReturned(
	        RentalAlreadyReturnedException exception) {

	    Map<String, Object> response = new HashMap<>();

	    response.put("timestamp", LocalDateTime.now());
	    response.put("status", 409);
	    response.put("error", "Rental Already Returned");
	    response.put("message", exception.getMessage());

	    return ResponseEntity
	            .status(HttpStatus.CONFLICT)
	            .body(response);
	}
	
	@ExceptionHandler(IllegalArgumentException.class)
	public ResponseEntity<String> handleIllegalArgumentException(
	        IllegalArgumentException ex) {

	    return ResponseEntity
	            .status(HttpStatus.BAD_REQUEST)
	            .body(ex.getMessage());
	}

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationErrors(
            MethodArgumentNotValidException exception) {

        Map<String, String> fieldErrors = new HashMap<>();

        exception.getBindingResult()
                .getFieldErrors()
                .forEach(error ->
                    fieldErrors.put(
                        error.getField(),
                        error.getDefaultMessage()
                    )
                );

        Map<String, Object> response = new HashMap<>();

        response.put("timestamp", LocalDateTime.now());
        response.put("status", 400);
        response.put("error", "Validation Failed");
        response.put("message", fieldErrors);

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(response);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Map<String, Object>> handleDatabaseError(
            DataIntegrityViolationException exception) {

        Map<String, Object> response = new HashMap<>();

        response.put("timestamp", LocalDateTime.now());
        response.put("status", 409);
        response.put("error", "Database Constraint Violation");
//        response.put("message", "Email or mobile number already exists");
        response.put("message", "A record with the same unique value already exists");

        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(response);
    }
}
