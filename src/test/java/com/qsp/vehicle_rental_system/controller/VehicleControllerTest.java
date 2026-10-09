package com.qsp.vehicle_rental_system.controller;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.qsp.vehicle_rental_system.dto.VehicleRequest;
import com.qsp.vehicle_rental_system.dto.VehicleResponse;
import com.qsp.vehicle_rental_system.entity.Vehicle;
import com.qsp.vehicle_rental_system.service.VehicleService;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class VehicleControllerTest {

    @Mock
    VehicleService vehicleService;

    @InjectMocks
    VehicleController vehicleController;

    MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(vehicleController)
                .build();
    }
    
    @Test
    void testSaveVehicle() throws Exception {

        VehicleRequest request = new VehicleRequest();
        request.setVname("Swift");
        request.setRentPerDay(1000.0);
        request.setVehicleNumber("GJ01AB1234");
        request.setCompany("Maruti");

        Vehicle vehicle = new Vehicle();
        vehicle.setVid(301);
        vehicle.setVname("Swift");
        vehicle.setRentPerDay(1000.0);
        vehicle.setVehicleNumber("GJ01AB1234");
        vehicle.setCompany("Maruti");
        vehicle.setAvailable(true);
        vehicle.setActive(true);

        when(vehicleService.saveVehicleService(any(VehicleRequest.class)))
                .thenReturn(vehicle);

        mockMvc.perform(
                post("/vehicles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {
                                "vname": "Swift",
                                "rentPerDay": 1000,
                                "vehicleNumber": "GJ01AB1234",
                                "company": "Maruti"
                            }
                        """)
            )
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.vid").value(301))
            .andExpect(jsonPath("$.vname").value("Swift"))
            .andExpect(jsonPath("$.rentPerDay").value(1000.0))
            .andExpect(jsonPath("$.vehicleNumber").value("GJ01AB1234"))
            .andExpect(jsonPath("$.company").value("Maruti"));

        verify(vehicleService).saveVehicleService(any(VehicleRequest.class));
    }
    
    @Test
    void testGetAllVehicles() throws Exception {

        VehicleResponse vehicle1 = new VehicleResponse();
        vehicle1.setVid(301);
        vehicle1.setVname("Swift");
        vehicle1.setRentPerDay(1000.0);
        vehicle1.setVehicleNumber("GJ01AB1234");
        vehicle1.setCompany("Maruti");
        vehicle1.setAvailable(true);
        vehicle1.setActive(true);

        VehicleResponse vehicle2 = new VehicleResponse();
        vehicle2.setVid(302);
        vehicle2.setVname("Creta");
        vehicle2.setRentPerDay(2000.0);
        vehicle2.setVehicleNumber("GJ02CD5678");
        vehicle2.setCompany("Hyundai");
        vehicle2.setAvailable(true);
        vehicle2.setActive(true);

        when(vehicleService.getAllVehiclesService())
                .thenReturn(List.of(vehicle1, vehicle2));

        mockMvc.perform(get("/vehicles"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].vid").value(301))
                .andExpect(jsonPath("$[0].vname").value("Swift"))
                .andExpect(jsonPath("$[1].vid").value(302))
                .andExpect(jsonPath("$[1].vname").value("Creta"));

        verify(vehicleService).getAllVehiclesService();
    }
    
    @Test
    void testGetVehicleById() throws Exception {

        VehicleResponse vehicle = new VehicleResponse();
        vehicle.setVid(301);
        vehicle.setVname("Swift");
        vehicle.setRentPerDay(1000.0);
        vehicle.setVehicleNumber("GJ01AB1234");
        vehicle.setCompany("Maruti");
        vehicle.setAvailable(true);
        vehicle.setActive(true);

        when(vehicleService.getVehicleByIdService(301))
                .thenReturn(vehicle);

        mockMvc.perform(get("/vehicles/301"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.vid").value(301))
                .andExpect(jsonPath("$.vname").value("Swift"))
                .andExpect(jsonPath("$.rentPerDay").value(1000.0))
                .andExpect(jsonPath("$.vehicleNumber").value("GJ01AB1234"))
                .andExpect(jsonPath("$.company").value("Maruti"))
                .andExpect(jsonPath("$.available").value(true))
                .andExpect(jsonPath("$.active").value(true));

        verify(vehicleService).getVehicleByIdService(301);
    }
    
    @Test
    void testDeleteVehicle() throws Exception {

        doNothing()
                .when(vehicleService)
                .deleteVehicleService(301);

        mockMvc.perform(delete("/vehicles/301"))
                .andExpect(status().isNoContent());

        verify(vehicleService)
                .deleteVehicleService(301);
    }
}
