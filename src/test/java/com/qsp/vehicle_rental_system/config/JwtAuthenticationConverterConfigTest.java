package com.qsp.vehicle_rental_system.config;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Collection;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;

class JwtAuthenticationConverterConfigTest {

    JwtAuthenticationConverter converter;

    @BeforeEach
    void setUp() {

        JwtAuthenticationConverterConfig config =
                new JwtAuthenticationConverterConfig();

        converter = config.jwtAuthenticationConverter();
    }


    @Test
    void testCustomerRole() {

        Jwt jwt = Jwt.withTokenValue("dummy-token")
                .header("alg", "HS256")
                .claim("role", "CUSTOMER")
                .subject("shubh@gmail.com")
                .build();

        Collection<GrantedAuthority> authorities =
                converter.convert(jwt).getAuthorities();

        assertTrue(
                authorities.stream()
                        .anyMatch(authority ->
                                authority.getAuthority()
                                        .equals("ROLE_CUSTOMER"))
        );
    }


    @Test
    void testAdminRole() {

        Jwt jwt = Jwt.withTokenValue("dummy-token")
                .header("alg", "HS256")
                .claim("role", "ADMIN")
                .subject("admin@gmail.com")
                .build();

        Collection<GrantedAuthority> authorities =
                converter.convert(jwt).getAuthorities();

        assertTrue(
                authorities.stream()
                        .anyMatch(authority ->
                                authority.getAuthority()
                                        .equals("ROLE_ADMIN"))
        );
    }
}