package com.qsp.vehicle_rental_system.controller;


import java.time.LocalDate;
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

import com.qsp.vehicle_rental_system.dto.RentalRequest;
import com.qsp.vehicle_rental_system.dto.RentalResponse;
import com.qsp.vehicle_rental_system.service.RentalService;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class RentalControllerTest {

    @Mock
    RentalService rentalService;

    @InjectMocks
    RentalController rentalController;

    MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(rentalController)
                .build();
    }
    
    @Test
    void testRentVehicle() throws Exception {

        RentalResponse response = new RentalResponse();
        response.setRid(201);
        response.setStartDate(LocalDate.of(2026, 10, 3));
        response.setTotalAmount(0);

        when(rentalService.rentVehicleService(any(RentalRequest.class)))
                .thenReturn(response);

        mockMvc.perform(
                post("/rentals")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {
                                "vehicleId": 301,
                                "startDate": "2026-10-03"
                            }
                        """)
            )
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.rid").value(201))
            .andExpect(jsonPath("$.startDate").value("2026-10-03"))
            .andExpect(jsonPath("$.totalAmount").value(0));

        verify(rentalService)
                .rentVehicleService(any(RentalRequest.class));
    }
    
    @Test
    void testReturnVehicle() throws Exception {

        RentalResponse response = new RentalResponse();
        response.setRid(201);
        response.setStartDate(LocalDate.of(2026, 10, 1));
        response.setEndDate(LocalDate.of(2026, 10, 3));
        response.setTotalAmount(2000.0);

        when(rentalService.returnVehicleService(201))
                .thenReturn(response);

        mockMvc.perform(put("/rentals/201/return"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.rid").value(201))
                .andExpect(jsonPath("$.startDate").value("2026-10-01"))
                .andExpect(jsonPath("$.endDate").value("2026-10-03"))
                .andExpect(jsonPath("$.totalAmount").value(2000.0));

        verify(rentalService)
                .returnVehicleService(201);
    }
    
    @Test
    void testGetAllRentals() throws Exception {

        RentalResponse rental1 = new RentalResponse();
        rental1.setRid(201);
        rental1.setStartDate(LocalDate.of(2026, 10, 1));
        rental1.setTotalAmount(1000.0);

        RentalResponse rental2 = new RentalResponse();
        rental2.setRid(202);
        rental2.setStartDate(LocalDate.of(2026, 10, 2));
        rental2.setTotalAmount(2000.0);

        when(rentalService.getAllRentalsService())
                .thenReturn(List.of(rental1, rental2));

        mockMvc.perform(get("/rentals"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].rid").value(201))
                .andExpect(jsonPath("$[1].rid").value(202));

        verify(rentalService).getAllRentalsService();
    }
    
    @Test
    void testGetRentalById() throws Exception {

        RentalResponse response = new RentalResponse();
        response.setRid(201);
        response.setStartDate(LocalDate.of(2026, 10, 1));
        response.setTotalAmount(2000.0);

        when(rentalService.getRentalByIdService(201))
                .thenReturn(response);

        mockMvc.perform(get("/rentals/201"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.rid").value(201))
                .andExpect(jsonPath("$.startDate").value("2026-10-01"))
                .andExpect(jsonPath("$.totalAmount").value(2000.0));

        verify(rentalService)
                .getRentalByIdService(201);
    }
}

