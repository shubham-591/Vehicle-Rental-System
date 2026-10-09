package com.qsp.vehicle_rental_system.controller;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.qsp.vehicle_rental_system.dto.CustomerResponse;
import com.qsp.vehicle_rental_system.service.CustomerService;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class CustomerControllerTest {

    @Mock
    CustomerService customerService;

    @InjectMocks
    CustomerController customerController;

    MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(customerController)
                .build();
    }
    
    @Test
    void testGetAllCustomers() throws Exception {

        CustomerResponse customer1 = new CustomerResponse();
        customer1.setCid(101);
        customer1.setCname("Shubham");
        customer1.setEmail("shubham@gmail.com");
        customer1.setMobileNo("9876543210");

        CustomerResponse customer2 = new CustomerResponse();
        customer2.setCid(102);
        customer2.setCname("Rahul");
        customer2.setEmail("rahul@gmail.com");
        customer2.setMobileNo("9876543211");

        when(customerService.getAllCustomersService())
                .thenReturn(List.of(customer1, customer2));

        mockMvc.perform(get("/customers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].cid").value(101))
                .andExpect(jsonPath("$[0].cname").value("Shubham"));

        verify(customerService).getAllCustomersService();
    }
    
    @Test
    void testGetCustomerById() throws Exception {

        CustomerResponse customer = new CustomerResponse();
        customer.setCid(101);
        customer.setCname("Shubham");
        customer.setEmail("shubham@gmail.com");
        customer.setMobileNo("9876543210");

        when(customerService.getCustomerByIdService(101))
                .thenReturn(customer);

        mockMvc.perform(get("/customers/101"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cid").value(101))
                .andExpect(jsonPath("$.cname").value("Shubham"))
                .andExpect(jsonPath("$.email").value("shubham@gmail.com"))
                .andExpect(jsonPath("$.mobileNo").value("9876543210"));

        verify(customerService).getCustomerByIdService(101);
    }
    
    @Test
    void testDeleteCustomer() throws Exception {

        doNothing()
                .when(customerService)
                .deleteCustomerService(101);

        mockMvc.perform(delete("/customers/101"))
                .andExpect(status().isNoContent());

        verify(customerService)
                .deleteCustomerService(101);
    }
}
