package com.qsp.vehicle_rental_system.config;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class SecurityConfigTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void testRegisterIsPublic() throws Exception {

        mockMvc.perform(
                post("/auth/register")
        )
        .andExpect(status().isBadRequest());
    }
    
    @Test
    void testGetVehiclesWithoutAuthentication() throws Exception {

        mockMvc.perform(
                get("/vehicles")
        )
        .andExpect(status().isUnauthorized());
    }
    
    @Test
    void testGetVehiclesAsCustomer() throws Exception {

        mockMvc.perform(
                get("/vehicles")
                    .with(jwt().authorities(
                            () -> "ROLE_CUSTOMER"
                    ))
        )
        .andExpect(status().isNotFound());
    }
    
    @Test
    void testPostVehicleAsCustomer() throws Exception {

        mockMvc.perform(
                post("/vehicles")
                    .with(jwt().authorities(
                            () -> "ROLE_CUSTOMER"
                    ))
        )
        .andExpect(status().isForbidden());
    }
    
    @Test
    void testPostVehicleAsAdmin() throws Exception {

        mockMvc.perform(
                post("/vehicles")
                    .with(jwt().authorities(
                            () -> "ROLE_ADMIN"
                    ))
        )
        .andExpect(status().isBadRequest());
    }
    
    @Test
    void testDeleteVehicleAsCustomer() throws Exception {

        mockMvc.perform(
                delete("/vehicles/301")
                    .with(jwt().authorities(
                            () -> "ROLE_CUSTOMER"
                    ))
        )
        .andExpect(status().isForbidden());
    }
    
    @Test
    void testDeleteVehicleAsAdmin() throws Exception {

        int status = mockMvc.perform(
                delete("/vehicles/301")
                    .with(jwt().authorities(
                            () -> "ROLE_ADMIN"
                    ))
        ).andReturn().getResponse().getStatus();

        assertNotEquals(401, status);
        assertNotEquals(403, status);
    }
    
    @Test
    void testGetCustomersAsCustomer() throws Exception {

        mockMvc.perform(
                get("/customers")
                    .with(jwt().authorities(
                            () -> "ROLE_CUSTOMER"
                    ))
        )
        .andExpect(status().isForbidden());
    }
    
    @Test
    void testGetCustomersAsAdmin() throws Exception {

        int status = mockMvc.perform(
                get("/customers")
                    .with(jwt().authorities(
                            () -> "ROLE_ADMIN"
                    ))
        ).andReturn().getResponse().getStatus();

        assertNotEquals(401, status);
        assertNotEquals(403, status);
    }
    
    @Test
    void testDeleteCustomerAsCustomer() throws Exception {

        mockMvc.perform(
                delete("/customers/101")
                    .with(jwt().authorities(
                            () -> "ROLE_CUSTOMER"
                    ))
        )
        .andExpect(status().isForbidden());
    }
    
    @Test
    void testDeleteCustomerAsAdmin() throws Exception {

        int status = mockMvc.perform(
                delete("/customers/99999")
                    .with(jwt().authorities(
                            () -> "ROLE_ADMIN"
                    ))
        ).andReturn().getResponse().getStatus();

        assertNotEquals(401, status);
        assertNotEquals(403, status);
    }
    
    @Test
    void testPostRentalAsCustomer() throws Exception {

        mockMvc.perform(
                post("/rentals")
                    .with(jwt().authorities(
                            () -> "ROLE_CUSTOMER"
                    ))
        )
        .andExpect(status().isBadRequest());
    }
    
    @Test
    void testPostRentalAsAdmin() throws Exception {

        mockMvc.perform(
                post("/rentals")
                    .with(jwt().authorities(
                            () -> "ROLE_ADMIN"
                    ))
        )
        .andExpect(status().isForbidden());
    }
    
    @Test
    void testGetRentalsAsCustomer() throws Exception {

        int status = mockMvc.perform(
                get("/rentals")
                    .with(jwt().authorities(
                            () -> "ROLE_CUSTOMER"
                    ))
        ).andReturn().getResponse().getStatus();

        assertNotEquals(401, status);
        assertNotEquals(403, status);
    }
    
    @Test
    void testGetRentalsAsAdmin() throws Exception {

        int status = mockMvc.perform(
                get("/rentals")
                    .with(jwt().authorities(
                            () -> "ROLE_ADMIN"
                    ))
        ).andReturn().getResponse().getStatus();

        assertNotEquals(401, status);
        assertNotEquals(403, status);
    }
    
    @Test
    void testReturnRentalAsCustomer() throws Exception {

        int status = mockMvc.perform(
                put("/rentals/99999/return")
                    .with(jwt().authorities(
                            () -> "ROLE_CUSTOMER"
                    ))
        ).andReturn().getResponse().getStatus();

        assertNotEquals(401, status);
        assertNotEquals(403, status);
    }
    
    @Test
    void testReturnRentalAsAdmin() throws Exception {

        int status = mockMvc.perform(
                put("/rentals/99999/return")
                    .with(jwt().authorities(
                            () -> "ROLE_ADMIN"
                    ))
        ).andReturn().getResponse().getStatus();

        assertNotEquals(401, status);
        assertNotEquals(403, status);
    }
    
    @Test
    void testPostRentalWithoutAuthentication() throws Exception {

        mockMvc.perform(
                post("/rentals")
        )
        .andExpect(status().isUnauthorized());
    }
    
    @Test
    void testGetCustomersWithoutAuthentication() throws Exception {

        mockMvc.perform(
                get("/customers")
        )
        .andExpect(status().isUnauthorized());
    }
    
    @Test
    void testLoginIsPublic() throws Exception {

        mockMvc.perform(
                post("/auth/login")
        )
        .andExpect(status().isBadRequest());
    }
    
    @Test
    void testGetVehiclesAsAdmin() throws Exception {

        int status = mockMvc.perform(
                get("/vehicles")
                    .with(jwt().authorities(
                            () -> "ROLE_ADMIN"
                    ))
        ).andReturn().getResponse().getStatus();

        assertNotEquals(401, status);
        assertNotEquals(403, status);
    }
}