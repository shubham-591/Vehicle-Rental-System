package com.qsp.vehicle_rental_system.exception;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.MethodParameter;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;


class GlobalExceptionHandlerTest {

    GlobalExceptionHandler exceptionHandler;

    @BeforeEach
    void setUp() {
        exceptionHandler = new GlobalExceptionHandler();
    }


    // 1. CustomerNotFoundException

    @Test
    void testHandleCustomerNotFound() {

        CustomerNotFoundException exception =
                new CustomerNotFoundException("Customer with id 101 not found");

        ResponseEntity<Map<String, Object>> response =
                exceptionHandler.handleCustomerNotFound(exception);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals(404, response.getBody().get("status"));
        assertEquals("Customer Not Found", response.getBody().get("error"));
        assertEquals("Customer with id 101 not found",
                response.getBody().get("message"));
    }


    // 2. VehicleNotFoundException

    @Test
    void testHandleVehicleNotFound() {

        VehicleNotFoundException exception =
                new VehicleNotFoundException("Vehicle with id 301 not found");

        ResponseEntity<Map<String, Object>> response =
                exceptionHandler.handleVehicleNotFound(exception);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals(404, response.getBody().get("status"));
        assertEquals("Vehicle Not Found", response.getBody().get("error"));
        assertEquals("Vehicle with id 301 not found",
                response.getBody().get("message"));
    }


    // 3. RentalNotFoundException

    @Test
    void testHandleRentalNotFound() {

        RentalNotFoundException exception =
                new RentalNotFoundException("Rental with id 201 not found");

        ResponseEntity<Map<String, Object>> response =
                exceptionHandler.handleRentalNotFound(exception);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals(404, response.getBody().get("status"));
        assertEquals("Rental Not Found", response.getBody().get("error"));
        assertEquals("Rental with id 201 not found",
                response.getBody().get("message"));
    }


    // 4. VehicleNotAvailableException

    @Test
    void testHandleVehicleNotAvailable() {

        VehicleNotAvailableException exception =
                new VehicleNotAvailableException("Vehicle is not available");

        ResponseEntity<Map<String, Object>> response =
                exceptionHandler.handleVehicleNotAvailable(exception);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals(409, response.getBody().get("status"));
        assertEquals("Vehicle Not Available",
                response.getBody().get("error"));
        assertEquals("Vehicle is not available",
                response.getBody().get("message"));
    }


    // 5. CustomerHasActiveRentalsException

    @Test
    void testHandleCustomerHasActiveRentalsException() {

        CustomerHasActiveRentalsException exception =
                new CustomerHasActiveRentalsException(
                        "Customer has active rentals");

        ResponseEntity<String> response =
                exceptionHandler.handleCustomerHasActiveRentalsException(exception);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals(409, response.getStatusCode().value());
        assertEquals("Customer has active rentals", response.getBody());
    }


    // 6. RentalAlreadyReturnedException

    @Test
    void testHandleRentalAlreadyReturned() {

        RentalAlreadyReturnedException exception =
                new RentalAlreadyReturnedException(
                        "Rental has already been returned");

        ResponseEntity<Map<String, Object>> response =
                exceptionHandler.handleRentalAlreadyReturned(exception);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals(409, response.getBody().get("status"));
        assertEquals("Rental Already Returned",
                response.getBody().get("error"));
        assertEquals("Rental has already been returned",
                response.getBody().get("message"));
    }


    // 7. IllegalArgumentException

    @Test
    void testHandleIllegalArgumentException() {

        IllegalArgumentException exception =
                new IllegalArgumentException("Start date cannot be in the past");

        ResponseEntity<String> response =
                exceptionHandler.handleIllegalArgumentException(exception);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(400, response.getStatusCode().value());
        assertEquals("Start date cannot be in the past",
                response.getBody());
    }


    // 8. HttpMessageNotReadableException

    @Test
    void testHandleHttpMessageNotReadable() {

        HttpMessageNotReadableException exception =
                new HttpMessageNotReadableException(
                        "Invalid request body",
                        (HttpInputMessage) null);

        ResponseEntity<String> response =
                exceptionHandler.handleHttpMessageNotReadable(exception);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(400, response.getStatusCode().value());
        assertEquals("Invalid request body", response.getBody());
    }


    // 9. UserNotFoundException

    @Test
    void testHandleUserNotFound() {

        UserNotFoundException exception =
                new UserNotFoundException("User not found");

        ResponseEntity<Map<String, Object>> response =
                exceptionHandler.handleUserNotFound(exception);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals(404, response.getBody().get("status"));
        assertEquals("User Not Found", response.getBody().get("error"));
        assertEquals("User not found",
                response.getBody().get("message"));
    }


    // 10. CustomerProfileNotFoundException

    @Test
    void testHandleCustomerProfileNotFound() {

        CustomerProfileNotFoundException exception =
                new CustomerProfileNotFoundException(
                        "Customer profile not found");

        ResponseEntity<Map<String, Object>> response =
                exceptionHandler.handleCustomerProfileNotFound(exception);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals(404, response.getBody().get("status"));
        assertEquals("Customer Profile Not Found",
                response.getBody().get("error"));
        assertEquals("Customer profile not found",
                response.getBody().get("message"));
    }


    // 11. InvalidCredentialsException

    @Test
    void testHandleInvalidCredentials() {

        InvalidCredentialsException exception =
                new InvalidCredentialsException("Invalid email or password");

        ResponseEntity<Map<String, Object>> response =
                exceptionHandler.handleInvalidCredentials(exception);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals(401, response.getBody().get("status"));
        assertEquals("Unauthorized", response.getBody().get("error"));
        assertEquals("Invalid email or password",
                response.getBody().get("message"));
    }


    // 12. MethodArgumentNotValidException

    @Test
    void testHandleValidationErrors() {

        BindingResult bindingResult = mock(BindingResult.class);
        MethodParameter methodParameter = mock(MethodParameter.class);

        FieldError fieldError =
                new FieldError(
                        "customer",
                        "email",
                        "Email must be valid");

        when(bindingResult.getFieldErrors())
                .thenReturn(List.of(fieldError));

        MethodArgumentNotValidException exception =
                new MethodArgumentNotValidException(
                        methodParameter,
                        bindingResult);

        ResponseEntity<Map<String, Object>> response =
                exceptionHandler.handleValidationErrors(exception);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(400, response.getBody().get("status"));
        assertEquals("Validation Failed",
                response.getBody().get("error"));

        Map<String, String> fieldErrors =
                (Map<String, String>) response.getBody().get("message");

        assertEquals("Email must be valid",
                fieldErrors.get("email"));
    }


    // 13. DataIntegrityViolationException

    @Test
    void testHandleDatabaseError() {

        DataIntegrityViolationException exception =
                new DataIntegrityViolationException(
                        "Duplicate email");

        ResponseEntity<Map<String, Object>> response =
                exceptionHandler.handleDatabaseError(exception);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals(409, response.getBody().get("status"));
        assertEquals("Database Constraint Violation",
                response.getBody().get("error"));
        assertEquals(
                "A record with the same unique value already exists",
                response.getBody().get("message"));
    }


    // 14. AccessDeniedException

    @Test
    void testHandleAccessDenied() {

        AccessDeniedException exception =
                new AccessDeniedException("Access denied");

        ResponseEntity<Map<String, Object>> response =
                exceptionHandler.handleAccessDenied(exception);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertEquals(403, response.getBody().get("status"));
        assertEquals("Forbidden", response.getBody().get("error"));
        assertEquals("Access denied",
                response.getBody().get("message"));
    }


    // 15. General Exception

    @Test
    void testHandleGeneralException() {

        Exception exception =
                new Exception("Something unexpected happened");

        ResponseEntity<Map<String, Object>> response =
                exceptionHandler.handleGeneralException(exception);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR,
                response.getStatusCode());

        assertEquals(500, response.getBody().get("status"));

        assertEquals("Internal Server Error",
                response.getBody().get("error"));

        assertEquals("Something went wrong on the server",
                response.getBody().get("message"));
    }
}