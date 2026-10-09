package com.qsp.vehicle_rental_system.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.qsp.vehicle_rental_system.dto.CustomerResponse;
import com.qsp.vehicle_rental_system.entity.Customer;
import com.qsp.vehicle_rental_system.entity.Rental;
import com.qsp.vehicle_rental_system.exception.CustomerHasActiveRentalsException;
import com.qsp.vehicle_rental_system.exception.CustomerNotFoundException;
import com.qsp.vehicle_rental_system.repository.CustomerRepository;
import com.qsp.vehicle_rental_system.repository.RentalRepository;

@ExtendWith(MockitoExtension.class)
public class CustomerServiceTest {

    @Mock
    CustomerRepository customerRepository;

    @Mock
    RentalRepository rentalRepository;

    @InjectMocks
    CustomerService customerService;
    
    Customer customer;
    
    @BeforeEach
    void setUp() {

        customer = new Customer();

        customer.setCid(101);
        customer.setCname("Shubham");
        customer.setEmail("shubham@gmail.com");
        customer.setMobileNo("9876543210");
        customer.setActive(true);
    }

    @Test
    void testGetCustomerById() {

        when(customerRepository.findById(101))
                .thenReturn(Optional.of(customer));

        CustomerResponse response =
                customerService.getCustomerByIdService(101);

        assertEquals(101, response.getCid());
        assertEquals("Shubham", response.getCname());
        assertEquals("shubham@gmail.com", response.getEmail());
    }
    
    @Test
    void testGetCustomerById_CustomerNotFound() {

        when(customerRepository.findById(999))
                .thenReturn(Optional.empty());

        assertThrows(
                CustomerNotFoundException.class,
                () -> customerService.getCustomerByIdService(999)
        );
    }
    
    @Test
    void testDeleteCustomer_HasActiveRental() {

        Rental rental = new Rental();
        rental.setEndDate(null); // vehicle is still rented

        when(customerRepository.findById(101))
                .thenReturn(Optional.of(customer));

        when(rentalRepository.findByCustomer(customer))
                .thenReturn(List.of(rental));

        assertThrows(
                CustomerHasActiveRentalsException.class,
                () -> customerService.deleteCustomerService(101)
        );
        
        verify(customerRepository, never()).save(customer);
    }
    
    @Test
    void testDeleteCustomer_AllRentalsReturned() {


        Rental rental1 = new Rental();
        rental1.setEndDate(LocalDate.of(2026, 9, 20));

        Rental rental2 = new Rental();
        rental2.setEndDate(LocalDate.of(2026, 9, 25));

        when(customerRepository.findById(101))
                .thenReturn(Optional.of(customer));

        when(rentalRepository.findByCustomer(customer))
                .thenReturn(List.of(rental1, rental2));

        customerService.deleteCustomerService(101);

        assertFalse(customer.isActive());

        verify(customerRepository).save(customer);
    }
    
    @Test
    void testGetAllCustomers() {

        Customer customer2 = new Customer();
        customer2.setCid(102);
        customer2.setCname("Rahul");
        customer2.setEmail("rahul@gmail.com");
        customer2.setMobileNo("9876543211");

        when(customerRepository.findAll())
                .thenReturn(List.of(customer, customer2));

        List<CustomerResponse> response =
                customerService.getAllCustomersService();

        assertEquals(2, response.size());
        assertEquals("Shubham", response.get(0).getCname());
        assertEquals("Rahul", response.get(1).getCname());
    }
}
