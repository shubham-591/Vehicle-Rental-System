package com.qsp.vehicle_rental_system.service;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.any;

import com.qsp.vehicle_rental_system.entity.Users;
import com.qsp.vehicle_rental_system.entity.Role;
import com.qsp.vehicle_rental_system.entity.Vehicle;
import com.qsp.vehicle_rental_system.exception.VehicleNotAvailableException;
import com.qsp.vehicle_rental_system.exception.VehicleNotFoundException;
import com.qsp.vehicle_rental_system.dto.VehicleRequest;
import com.qsp.vehicle_rental_system.dto.VehicleResponse;

import com.qsp.vehicle_rental_system.repository.VehicleRepository;

@ExtendWith(MockitoExtension.class)
public class VehicleServiceTest {

    @Mock
    VehicleRepository vehicleRepository;

    @Mock
    CurrentUserService currentUserService;

    @InjectMocks
    VehicleService vehicleService;
    
    Vehicle vehicle1;
    Vehicle vehicle2;
    
    @BeforeEach
    void setUp() {

        vehicle1 = new Vehicle();
        vehicle1.setVid(301);
        vehicle1.setVname("Swift");
        vehicle1.setRentPerDay(1000);
        vehicle1.setVehicleNumber("GJ01AB1234");
        vehicle1.setCompany("Maruti");
        vehicle1.setAvailable(true);
        vehicle1.setActive(true);

        vehicle2 = new Vehicle();
        vehicle2.setVid(302);
        vehicle2.setVname("Creta");
        vehicle2.setRentPerDay(2000);
        vehicle2.setVehicleNumber("GJ02CD5678");
        vehicle2.setCompany("Hyundai");
        vehicle2.setAvailable(true);
        vehicle2.setActive(false);
    }
    
    @Test
    void testGetAllVehicles_Admin() {

        Users admin = new Users();
        admin.setRole(Role.ADMIN);

        when(currentUserService.getLoggedInUser())
                .thenReturn(admin);

        when(vehicleRepository.findAll())
                .thenReturn(List.of(vehicle1, vehicle2));

        List<VehicleResponse> response =
                vehicleService.getAllVehiclesService();

        assertEquals(2, response.size());
        assertEquals("Swift", response.get(0).getVname());
        assertEquals("Creta", response.get(1).getVname());

        verify(vehicleRepository).findAll();
    }
    
    @Test
    void testGetAllVehicles_Customer() {

        Users customer = new Users();
        customer.setRole(Role.CUSTOMER);

        when(currentUserService.getLoggedInUser())
                .thenReturn(customer);

        when(vehicleRepository.findByActiveTrue())
                .thenReturn(List.of(vehicle1));

        List<VehicleResponse> response =
                vehicleService.getAllVehiclesService();

        assertEquals(1, response.size());
        assertEquals("Swift", response.get(0).getVname());
        assertTrue(response.get(0).isActive());

        verify(vehicleRepository).findByActiveTrue();
        verify(vehicleRepository, never()).findAll();
    }
    
    @Test
    void testGetVehicleById_Admin_ActiveVehicle() {

        Users admin = new Users();
        admin.setRole(Role.ADMIN);

        when(currentUserService.getLoggedInUser())
                .thenReturn(admin);

        when(vehicleRepository.findById(301))
                .thenReturn(Optional.of(vehicle1));

        VehicleResponse response =
                vehicleService.getVehicleByIdService(301);

        assertEquals(301, response.getVid());
        assertEquals("Swift", response.getVname());
        assertTrue(response.isActive());

        verify(vehicleRepository).findById(301);
    }
    
    @Test
    void testGetVehicleById_Admin_InactiveVehicle() {

        Users admin = new Users();
        admin.setRole(Role.ADMIN);

        when(currentUserService.getLoggedInUser())
                .thenReturn(admin);

        when(vehicleRepository.findById(302))
                .thenReturn(Optional.of(vehicle2));

        VehicleResponse response =
                vehicleService.getVehicleByIdService(302);

        assertEquals(302, response.getVid());
        assertEquals("Creta", response.getVname());
        assertFalse(response.isActive());

        verify(vehicleRepository).findById(302);
    }
    
    @Test
    void testGetVehicleById_Customer_InactiveVehicle() {

        Users customer = new Users();
        customer.setRole(Role.CUSTOMER);

        when(currentUserService.getLoggedInUser())
                .thenReturn(customer);

        when(vehicleRepository.findById(302))
                .thenReturn(Optional.of(vehicle2));

        assertThrows(
                VehicleNotFoundException.class,
                () -> vehicleService.getVehicleByIdService(302)
        );

        verify(vehicleRepository).findById(302);
    }
    
    @Test
    void testGetVehicleById_VehicleNotFound() {

        Users admin = new Users();
        admin.setRole(Role.ADMIN);

        when(currentUserService.getLoggedInUser())
                .thenReturn(admin);

        when(vehicleRepository.findById(999))
                .thenReturn(Optional.empty());

        assertThrows(
                VehicleNotFoundException.class,
                () -> vehicleService.getVehicleByIdService(999)
        );

        verify(vehicleRepository).findById(999);
    }
    
    @Test
    void testDeleteVehicle_AvailableVehicle() {

        when(vehicleRepository.findById(301))
                .thenReturn(Optional.of(vehicle1));

        vehicleService.deleteVehicleService(301);

        assertFalse(vehicle1.isActive());

        verify(vehicleRepository).save(vehicle1);
    }
    
    @Test
    void testDeleteVehicle_CurrentlyRented() {

        vehicle1.setAvailable(false);

        when(vehicleRepository.findById(301))
                .thenReturn(Optional.of(vehicle1));

        assertThrows(
                VehicleNotAvailableException.class,
                () -> vehicleService.deleteVehicleService(301)
        );

        verify(vehicleRepository, never()).save(vehicle1);
    }
    
    @Test
    void testSaveVehicle_Success() {

        VehicleRequest request = new VehicleRequest();

        request.setVname("Baleno");
        request.setRentPerDay(1500.0);
        request.setVehicleNumber("GJ03EF1234");
        request.setCompany("Maruti");

        Vehicle savedVehicle = new Vehicle();

        savedVehicle.setVid(303);
        savedVehicle.setVname("Baleno");
        savedVehicle.setRentPerDay(1500.0);
        savedVehicle.setVehicleNumber("GJ03EF1234");
        savedVehicle.setCompany("Maruti");
        savedVehicle.setAvailable(true);
        savedVehicle.setActive(true);

        when(vehicleRepository.save(any(Vehicle.class)))
                .thenReturn(savedVehicle);

        Vehicle response =
                vehicleService.saveVehicleService(request);

        assertEquals(303, response.getVid());
        assertEquals("Baleno", response.getVname());
        assertEquals(1500.0, response.getRentPerDay());
        assertTrue(response.isActive());
        assertTrue(response.isAvailable());

        verify(vehicleRepository).save(any(Vehicle.class));
    }

}
