package com.qsp.vehicle_rental_system.config;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;


class JwtConfigTest {

    JwtConfig jwtConfig;

    @BeforeEach
    void setUp() {

        jwtConfig = new JwtConfig();

        ReflectionTestUtils.setField(
                jwtConfig,
                "secretKey",
                "mySecretKeyForJwtTesting1234567890"
        );
    }


    @Test
    void testJwtEncoder() {

        JwtEncoder encoder = jwtConfig.jwtEncoder();

        assertNotNull(encoder);
    }


    @Test
    void testJwtDecoder() {

        JwtDecoder decoder = jwtConfig.jwtDecoder();

        assertNotNull(decoder);
    }
}