package com.qsp.vehicle_rental_system.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.qsp.vehicle_rental_system.dto.LoginRequest;
import com.qsp.vehicle_rental_system.dto.RegisterRequest;
import com.qsp.vehicle_rental_system.dto.UserResponse;
import com.qsp.vehicle_rental_system.entity.Customer;
import com.qsp.vehicle_rental_system.entity.Role;
import com.qsp.vehicle_rental_system.entity.Users;
import com.qsp.vehicle_rental_system.exception.InvalidCredentialsException;
import com.qsp.vehicle_rental_system.repository.CustomerRepository;
import com.qsp.vehicle_rental_system.repository.UsersRepository;

@ExtendWith(MockitoExtension.class)
public class UsersServiceTest {
	
	@Mock
	UsersRepository userRepository;

	@Mock
	CustomerRepository customerRepository;

	@Mock
	JwtService jwtService;

	@InjectMocks
	UsersService usersService;

	RegisterRequest request;
	Users user;
	Customer customer;
	
	@BeforeEach
	void setUp() {

	    request = new RegisterRequest();
	    request.setName("Shubham");
	    request.setEmail("shubham@gmail.com");
	    request.setPassword("password123");
	    request.setMobileNo("9876543210");

	    user = new Users();
	    user.setUid(1);
	    user.setName("Shubham");
	    user.setEmail("shubham@gmail.com");
	    user.setRole(Role.CUSTOMER);
	    user.setPassword(
	            new BCryptPasswordEncoder().encode("password123")
	    );

	    customer = new Customer();
	    customer.setCid(101);
	    customer.setCname("Shubham");
	    customer.setEmail("shubham@gmail.com");
	    customer.setMobileNo("9876543210");
	    customer.setActive(true);
	    user.setCustomer(customer);  
	}
	
	@Test
	void testRegisterUser_Success() {

	    when(customerRepository.save(any(Customer.class)))
	            .thenReturn(customer);

	    when(userRepository.save(any(Users.class)))
	            .thenReturn(user);

	    UserResponse response =
	            usersService.registerUserService(request);

	    assertEquals(1, response.getUid());
	    assertEquals("Shubham", response.getName());
	    assertEquals("shubham@gmail.com", response.getEmail());
	    assertEquals(Role.CUSTOMER, response.getRole());
	    assertEquals(101, response.getCustomerId());

	    verify(customerRepository).save(any(Customer.class));
	    verify(userRepository).save(any(Users.class));
	}
	
	@Test
	void testRegisterUser_PasswordIsEncrypted() {

	    when(customerRepository.save(any(Customer.class)))
	            .thenReturn(customer);

	    when(userRepository.save(any(Users.class)))
	            .thenReturn(user);

	    usersService.registerUserService(request);

	    ArgumentCaptor<Users> captor =
	            ArgumentCaptor.forClass(Users.class);

	    verify(userRepository).save(captor.capture());

	    Users savedUser = captor.getValue();

	    assertNotEquals(
	            request.getPassword(),
	            savedUser.getPassword()
	    );
	}
	
	@Test
	void testLoginUser_Success() {

	    LoginRequest request = new LoginRequest();
	    request.setEmail("shubham@gmail.com");
	    request.setPassword("password123");

	    when(userRepository.findByEmail("shubham@gmail.com"))
	            .thenReturn(Optional.of(user));

	    when(jwtService.generateToken(user))
	            .thenReturn("mock-jwt-token");

	    String token = usersService.loginUser(request);

	    assertEquals("mock-jwt-token", token);

	    verify(userRepository).findByEmail("shubham@gmail.com");
	    verify(jwtService).generateToken(user);
	}
	
	@Test
	void testLoginUser_WrongPassword() {

	    LoginRequest request = new LoginRequest();
	    request.setEmail("shubham@gmail.com");
	    request.setPassword("wrongpassword");

	    when(userRepository.findByEmail("shubham@gmail.com"))
	            .thenReturn(Optional.of(user));

	    assertThrows(
	            InvalidCredentialsException.class,
	            () -> usersService.loginUser(request)
	    );

	    verify(jwtService, never()).generateToken(user);
	}
	
	@Test
	void testLoginUser_UserNotFound() {

	    LoginRequest request = new LoginRequest();
	    request.setEmail("unknown@gmail.com");
	    request.setPassword("password123");

	    when(userRepository.findByEmail("unknown@gmail.com"))
	            .thenReturn(Optional.empty());

	    assertThrows(
	            InvalidCredentialsException.class,
	            () -> usersService.loginUser(request)
	    );

	    verify(jwtService, never()).generateToken(any(Users.class));
	}
	
	@Test
	void testLoginUser_InactiveCustomer() {

	    customer.setActive(false);

	    LoginRequest request = new LoginRequest();
	    request.setEmail("shubham@gmail.com");
	    request.setPassword("password123");

	    when(userRepository.findByEmail("shubham@gmail.com"))
	            .thenReturn(Optional.of(user));

	    assertThrows(
	            AccessDeniedException.class,
	            () -> usersService.loginUser(request)
	    );

	    verify(jwtService, never()).generateToken(any(Users.class));
	}
}
