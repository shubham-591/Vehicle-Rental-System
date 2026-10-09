package com.qsp.vehicle_rental_system.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Duration;
import java.time.Instant;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;

import com.qsp.vehicle_rental_system.entity.Role;
import com.qsp.vehicle_rental_system.entity.Users;
import com.qsp.vehicle_rental_system.service.JwtService;

@ExtendWith(MockitoExtension.class)
class JwtServiceTest {

    @Mock
    JwtEncoder jwtEncoder;

    @InjectMocks
    JwtService jwtService;

    Users user;

    @BeforeEach
    void setUp() {

        user = new Users();

        user.setName("Shubham");
        user.setEmail("shubh@gmail.com");
        user.setPassword("password123");
        user.setRole(Role.CUSTOMER);
    }


    @Test
    void testGenerateToken() {

        Jwt jwt = mock(Jwt.class);

        when(jwt.getTokenValue())
                .thenReturn("dummy-jwt-token");

        when(jwtEncoder.encode(any(JwtEncoderParameters.class)))
                .thenReturn(jwt);


        String token = jwtService.generateToken(user);


        assertNotNull(token);
        assertEquals("dummy-jwt-token", token);


        ArgumentCaptor<JwtEncoderParameters> captor =
                ArgumentCaptor.forClass(JwtEncoderParameters.class);

        verify(jwtEncoder).encode(captor.capture());


        JwtClaimsSet claims =
                captor.getValue().getClaims();


        assertEquals("shubh@gmail.com",
                claims.getSubject());

        assertEquals("CUSTOMER",
                claims.getClaimAsString("role"));

        assertNotNull(claims.getIssuedAt());
        assertNotNull(claims.getExpiresAt());
    }


    @Test
    void testTokenExpiryIsOneHour() {

        Jwt jwt = mock(Jwt.class);

        when(jwt.getTokenValue())
                .thenReturn("dummy-jwt-token");

        when(jwtEncoder.encode(any(JwtEncoderParameters.class)))
                .thenReturn(jwt);


        Instant before = Instant.now();

        jwtService.generateToken(user);

        Instant after = Instant.now();


        ArgumentCaptor<JwtEncoderParameters> captor =
                ArgumentCaptor.forClass(JwtEncoderParameters.class);

        verify(jwtEncoder).encode(captor.capture());


        JwtClaimsSet claims =
                captor.getValue().getClaims();

        Instant issuedAt = claims.getIssuedAt();
        Instant expiresAt = claims.getExpiresAt();


        assertTrue(
                !issuedAt.isBefore(before)
                        && !issuedAt.isAfter(after)
        );


        long expirySeconds =
                Duration.between(issuedAt, expiresAt).getSeconds();

        assertEquals(3600, expirySeconds);
    }
}