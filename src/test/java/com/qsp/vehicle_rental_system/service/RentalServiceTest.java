package com.qsp.vehicle_rental_system.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;

import com.qsp.vehicle_rental_system.dto.RentalRequest;
import com.qsp.vehicle_rental_system.dto.RentalResponse;
import com.qsp.vehicle_rental_system.entity.Customer;
import com.qsp.vehicle_rental_system.entity.Rental;
import com.qsp.vehicle_rental_system.entity.Role;
import com.qsp.vehicle_rental_system.entity.Users;
import com.qsp.vehicle_rental_system.entity.Vehicle;
import com.qsp.vehicle_rental_system.exception.CustomerProfileNotFoundException;
import com.qsp.vehicle_rental_system.exception.RentalAlreadyReturnedException;
import com.qsp.vehicle_rental_system.exception.RentalNotFoundException;
import com.qsp.vehicle_rental_system.exception.VehicleNotAvailableException;
import com.qsp.vehicle_rental_system.exception.VehicleNotFoundException;
import com.qsp.vehicle_rental_system.repository.RentalRepository;
import com.qsp.vehicle_rental_system.repository.VehicleRepository;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RentalServiceTest {

    @Mock
    RentalRepository rentalRepository;

    @Mock
    VehicleRepository vehicleRepository;

    @Mock
    CurrentUserService currentUserService;

    @InjectMocks
    RentalService rentalService;
    
    Customer customer;
    Users user;
    Vehicle vehicle1;
    Vehicle vehicle2;
    Rental rental1;
    
    @BeforeEach
    void setUp() {

        customer = new Customer();
        customer.setCid(101);
        customer.setCname("Shubham");
        customer.setActive(true);

        user = new Users();
        user.setRole(Role.CUSTOMER);
        user.setCustomer(customer);

        vehicle1 = new Vehicle();
        vehicle1.setVid(301);
        vehicle1.setVname("Swift");
        vehicle1.setRentPerDay(1000);
        vehicle1.setVehicleNumber("GJ01AB1234");
        vehicle1.setCompany("Maruti");
        vehicle1.setActive(true);
        vehicle1.setAvailable(true);

        vehicle2 = new Vehicle();
        vehicle2.setVid(302);
        vehicle2.setVname("Creta");
        vehicle2.setRentPerDay(2000);
        vehicle2.setVehicleNumber("GJ02CD5678");
        vehicle2.setCompany("Hyundai");
        vehicle2.setActive(true);
        vehicle2.setAvailable(true);
        
        rental1 = new Rental();
        rental1.setRid(201);
        rental1.setCustomer(customer);
        rental1.setVehicle(vehicle1);
        rental1.setStartDate(LocalDate.now().minusDays(2));
        rental1.setTotalAmount(0);
    }
    
    @Test
    void testRentVehicle_Success() {

        RentalRequest request = new RentalRequest();
        request.setVehicleId(301);
        request.setStartDate(LocalDate.now());

        when(currentUserService.getLoggedInUser())
                .thenReturn(user);

        when(vehicleRepository.findById(301))
                .thenReturn(Optional.of(vehicle1));

        Rental savedRental = new Rental();
        savedRental.setRid(201);
        savedRental.setCustomer(customer);
        savedRental.setVehicle(vehicle1);
        savedRental.setStartDate(request.getStartDate());

        when(rentalRepository.save(any(Rental.class)))
                .thenReturn(savedRental);

        RentalResponse response =
                rentalService.rentVehicleService(request);

        assertEquals(201, response.getRid());
        assertEquals(LocalDate.now(), response.getStartDate());
        assertEquals("Swift", response.getVehicle().getVname());
        assertFalse(response.getVehicle().isAvailable());

        verify(vehicleRepository).save(vehicle1);
        verify(rentalRepository).save(any(Rental.class));
    }
    
    @Test
    void testRentVehicle_VehicleAlreadyRented() {

        vehicle1.setAvailable(false);

        RentalRequest request = new RentalRequest();
        request.setVehicleId(301);
        request.setStartDate(LocalDate.now());

        when(currentUserService.getLoggedInUser())
                .thenReturn(user);

        when(vehicleRepository.findById(301))
                .thenReturn(Optional.of(vehicle1));

        assertThrows(
                VehicleNotAvailableException.class,
                () -> rentalService.rentVehicleService(request)
        );

        verify(rentalRepository, never()).save(any(Rental.class));
        verify(vehicleRepository, never()).save(vehicle1);
    }
    
    @Test
    void testRentVehicle_InactiveVehicle() {

        vehicle1.setActive(false);

        RentalRequest request = new RentalRequest();
        request.setVehicleId(301);
        request.setStartDate(LocalDate.now());

        when(currentUserService.getLoggedInUser())
                .thenReturn(user);

        when(vehicleRepository.findById(301))
                .thenReturn(Optional.of(vehicle1));

        assertThrows(
                VehicleNotAvailableException.class,
                () -> rentalService.rentVehicleService(request)
        );

        verify(rentalRepository, never()).save(any(Rental.class));
        verify(vehicleRepository, never()).save(vehicle1);
    }
    
    @Test
    void testRentVehicle_VehicleNotFound() {

        RentalRequest request = new RentalRequest();
        request.setVehicleId(999);
        request.setStartDate(LocalDate.now());

        when(currentUserService.getLoggedInUser())
                .thenReturn(user);

        when(vehicleRepository.findById(999))
                .thenReturn(Optional.empty());

        assertThrows(
                VehicleNotFoundException.class,
                () -> rentalService.rentVehicleService(request)
        );

        verify(rentalRepository, never()).save(any(Rental.class));
        verify(vehicleRepository, never()).save(any(Vehicle.class));
    }
    
    @Test
    void testRentVehicle_CustomerProfileNotFound() {

        user.setCustomer(null);

        RentalRequest request = new RentalRequest();
        request.setVehicleId(301);
        request.setStartDate(LocalDate.now());

        when(currentUserService.getLoggedInUser())
                .thenReturn(user);

        assertThrows(
                CustomerProfileNotFoundException.class,
                () -> rentalService.rentVehicleService(request)
        );

        verify(vehicleRepository, never()).findById(301);
        verify(vehicleRepository, never()).save(any(Vehicle.class));
        verify(rentalRepository, never()).save(any(Rental.class));
    }
    
    @Test
    void testRentVehicle_InactiveCustomer() {

        customer.setActive(false);

        RentalRequest request = new RentalRequest();
        request.setVehicleId(301);
        request.setStartDate(LocalDate.now());

        when(currentUserService.getLoggedInUser())
                .thenReturn(user);

        assertThrows(
                AccessDeniedException.class,
                () -> rentalService.rentVehicleService(request)
        );

        verify(vehicleRepository, never()).findById(301);
        verify(vehicleRepository, never()).save(any(Vehicle.class));
        verify(rentalRepository, never()).save(any(Rental.class));
    }
    
    @Test
    void testRentVehicle_StartDateInPast() {

        RentalRequest request = new RentalRequest();
        request.setVehicleId(301);
        request.setStartDate(LocalDate.now().minusDays(1));

        assertThrows(
                IllegalArgumentException.class,
                () -> rentalService.rentVehicleService(request)
        );

        verify(currentUserService, never()).getLoggedInUser();
        verify(vehicleRepository, never()).findById(301);
        verify(vehicleRepository, never()).save(any(Vehicle.class));
        verify(rentalRepository, never()).save(any(Rental.class));
    }
    
    @Test
    void testReturnVehicle_Success() {

        when(rentalRepository.findById(201))
                .thenReturn(Optional.of(rental1));

        when(currentUserService.getLoggedInUser())
                .thenReturn(user);

        when(rentalRepository.save(any(Rental.class)))
                .thenReturn(rental1);

        RentalResponse response =
                rentalService.returnVehicleService(201);

        assertEquals(201, response.getRid());
        assertEquals(LocalDate.now(), response.getEndDate());
        assertEquals(2000.0, response.getTotalAmount());
        assertTrue(response.getVehicle().isAvailable());

        verify(vehicleRepository).save(vehicle1);
        verify(rentalRepository).save(rental1);
    }
    
    @Test
    void testReturnVehicle_AlreadyReturned() {

        rental1.setEndDate(LocalDate.now().minusDays(1));

        when(rentalRepository.findById(201))
                .thenReturn(Optional.of(rental1));

        when(currentUserService.getLoggedInUser())
                .thenReturn(user);

        assertThrows(
                RentalAlreadyReturnedException.class,
                () -> rentalService.returnVehicleService(201)
        );

        verify(vehicleRepository, never()).save(any(Vehicle.class));
        verify(rentalRepository, never()).save(any(Rental.class));
    }
    
    @Test
    void testReturnVehicle_RentalNotFound() {

        when(rentalRepository.findById(999))
                .thenReturn(Optional.empty());

        assertThrows(
                RentalNotFoundException.class,
                () -> rentalService.returnVehicleService(999)
        );

        verify(currentUserService, never()).getLoggedInUser();
        verify(vehicleRepository, never()).save(any(Vehicle.class));
        verify(rentalRepository, never()).save(any(Rental.class));
    }
    
    @Test
    void testReturnVehicle_CustomerNotOwner() {

        Customer otherCustomer = new Customer();
        otherCustomer.setCid(102);

        user.setCustomer(otherCustomer);

        when(rentalRepository.findById(201))
                .thenReturn(Optional.of(rental1));

        when(currentUserService.getLoggedInUser())
                .thenReturn(user);

        assertThrows(
                AccessDeniedException.class,
                () -> rentalService.returnVehicleService(201)
        );

        verify(vehicleRepository, never()).save(any(Vehicle.class));
        verify(rentalRepository, never()).save(any(Rental.class));
    }
    
    @Test
    void testReturnVehicle_Admin() {

        user.setRole(Role.ADMIN);

        when(rentalRepository.findById(201))
                .thenReturn(Optional.of(rental1));

        when(currentUserService.getLoggedInUser())
                .thenReturn(user);

        when(rentalRepository.save(any(Rental.class)))
                .thenReturn(rental1);

        RentalResponse response =
                rentalService.returnVehicleService(201);

        assertEquals(201, response.getRid());
        assertEquals(LocalDate.now(), response.getEndDate());
        assertTrue(response.getVehicle().isAvailable());

        verify(vehicleRepository).save(vehicle1);
        verify(rentalRepository).save(rental1);
    }
    
    @Test
    void testGetAllRentals_Admin() {

        user.setRole(Role.ADMIN);

        when(currentUserService.getLoggedInUser())
                .thenReturn(user);

        when(rentalRepository.findAll())
                .thenReturn(List.of(rental1));

        List<RentalResponse> response =
                rentalService.getAllRentalsService();

        assertEquals(1, response.size());
        assertEquals(201, response.get(0).getRid());
        assertEquals("Swift", response.get(0).getVehicle().getVname());

        verify(rentalRepository).findAll();
        verify(rentalRepository, never()).findByCustomer(any(Customer.class));
    }
    
    @Test
    void testGetAllRentals_Customer() {

//        user.setRole(Role.CUSTOMER);

        when(currentUserService.getLoggedInUser())
                .thenReturn(user);

        when(rentalRepository.findByCustomer(customer))
                .thenReturn(List.of(rental1));

        List<RentalResponse> response =
                rentalService.getAllRentalsService();

        assertEquals(1, response.size());
        assertEquals(201, response.get(0).getRid());
        assertEquals("Swift", response.get(0).getVehicle().getVname());

        verify(rentalRepository).findByCustomer(customer);
        verify(rentalRepository, never()).findAll();
    }
    
    @Test
    void testGetRentalById_Admin() {

        user.setRole(Role.ADMIN);

        when(rentalRepository.findById(201))
                .thenReturn(Optional.of(rental1));

        when(currentUserService.getLoggedInUser())
                .thenReturn(user);

        RentalResponse response =
                rentalService.getRentalByIdService(201);

        assertEquals(201, response.getRid());
        assertEquals("Swift", response.getVehicle().getVname());

        verify(rentalRepository).findById(201);
    }
    
    @Test
    void testGetRentalById_CustomerOwnRental() {

        when(rentalRepository.findById(201))
                .thenReturn(Optional.of(rental1));

        when(currentUserService.getLoggedInUser())
                .thenReturn(user);

        RentalResponse response =
                rentalService.getRentalByIdService(201);

        assertEquals(201, response.getRid());
        assertEquals("Swift", response.getVehicle().getVname());

        verify(rentalRepository).findById(201);
    }
    
    @Test
    void testGetRentalById_CustomerNotOwner() {

        Customer otherCustomer = new Customer();
        otherCustomer.setCid(102);

        user.setCustomer(otherCustomer);

        when(rentalRepository.findById(201))
                .thenReturn(Optional.of(rental1));

        when(currentUserService.getLoggedInUser())
                .thenReturn(user);

        assertThrows(
                AccessDeniedException.class,
                () -> rentalService.getRentalByIdService(201)
        );

        verify(rentalRepository).findById(201);
    }

    @Test
    void testGetRentalById_RentalNotFound() {

        when(rentalRepository.findById(999))
                .thenReturn(Optional.empty());

        assertThrows(
                RentalNotFoundException.class,
                () -> rentalService.getRentalByIdService(999)
        );

        verify(currentUserService, never()).getLoggedInUser();
    }
}
