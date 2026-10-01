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
import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;

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
	
	@ExceptionHandler(CustomerHasActiveRentalsException.class)
	public ResponseEntity<String> handleCustomerHasActiveRentalsException(
	        CustomerHasActiveRentalsException ex) {

	    return ResponseEntity
	            .status(HttpStatus.CONFLICT)
	            .body(ex.getMessage());
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
	
	@ExceptionHandler(HttpMessageNotReadableException.class)
	public ResponseEntity<String> handleHttpMessageNotReadable(
	        HttpMessageNotReadableException ex) {

	    if (ex.getMostSpecificCause() instanceof UnrecognizedPropertyException) {

	        UnrecognizedPropertyException cause =
	                (UnrecognizedPropertyException) ex.getMostSpecificCause();

	        return ResponseEntity
	                .status(HttpStatus.BAD_REQUEST)
	                .body("Unknown field: " + cause.getPropertyName());
	    }

	    return ResponseEntity
	            .status(HttpStatus.BAD_REQUEST)
	            .body("Invalid request body");
	}
	
	@ExceptionHandler(UserNotFoundException.class)
	public ResponseEntity<Map<String, Object>> handleUserNotFound(
	        UserNotFoundException exception) {

	    Map<String, Object> response = new HashMap<>();

	    response.put("timestamp", LocalDateTime.now());
	    response.put("status", 404);
	    response.put("error", "User Not Found");
	    response.put("message", exception.getMessage());

	    return ResponseEntity
	            .status(HttpStatus.NOT_FOUND)
	            .body(response);
	}
	
	@ExceptionHandler(CustomerProfileNotFoundException.class)
	public ResponseEntity<Map<String, Object>> handleCustomerProfileNotFound(
	        CustomerProfileNotFoundException exception) {

	    Map<String, Object> response = new HashMap<>();

	    response.put("timestamp", LocalDateTime.now());
	    response.put("status", 404);
	    response.put("error", "Customer Profile Not Found");
	    response.put("message", exception.getMessage());

	    return ResponseEntity
	            .status(HttpStatus.NOT_FOUND)
	            .body(response);
	}
	
	@ExceptionHandler(InvalidCredentialsException.class)
	public ResponseEntity<Map<String, Object>> handleInvalidCredentials(
	        InvalidCredentialsException exception) {

	    Map<String, Object> response = new HashMap<>();

	    response.put("timestamp", LocalDateTime.now());
	    response.put("status", 401);
	    response.put("error", "Unauthorized");
	    response.put("message", exception.getMessage());

	    return ResponseEntity
	            .status(HttpStatus.UNAUTHORIZED)
	            .body(response);
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
    
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Map<String, Object>> handleAccessDenied(
            AccessDeniedException exception) {

        Map<String, Object> response = new HashMap<>();

        response.put("timestamp", LocalDateTime.now());
        response.put("status", 403);
        response.put("error", "Forbidden");
        response.put("message", exception.getMessage());

        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(response);
    }
    
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGeneralException(
            Exception exception) {

        Map<String, Object> response = new HashMap<>();

        response.put("timestamp", LocalDateTime.now());
        response.put("status", 500);
        response.put("error", "Internal Server Error");
        response.put("message", "Something went wrong on the server");

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(response);
    }
}
