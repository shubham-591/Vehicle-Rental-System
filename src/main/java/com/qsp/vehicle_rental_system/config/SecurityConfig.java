package com.qsp.vehicle_rental_system.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {
	
	private final JwtAuthenticationConverter jwtAuthenticationConverter;

    public SecurityConfig(JwtAuthenticationConverter jwtAuthenticationConverter) {
        this.jwtAuthenticationConverter = jwtAuthenticationConverter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http)
            throws Exception {

        http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/auth/register", "/auth/login").permitAll()
                
                // Customer APIs
                .requestMatchers(HttpMethod.GET, "/customers", "/customers/**")
                .hasRole("ADMIN")

                .requestMatchers(HttpMethod.DELETE, "/customers/**")
                .hasRole("ADMIN")
                
                // Vehicle APIs
                .requestMatchers(HttpMethod.POST, "/vehicles")
                    .hasRole("ADMIN")
                .requestMatchers(HttpMethod.GET, "/vehicles", "/vehicles/**")
                    .hasAnyRole("CUSTOMER", "ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/vehicles/**")
                    .hasRole("ADMIN")
                    
                // Rental APIs
                .requestMatchers(HttpMethod.POST, "/rentals")
                    .hasAnyRole("CUSTOMER", "ADMIN")

                .requestMatchers(HttpMethod.GET, "/rentals")
                    .hasAnyRole("ADMIN", "CUSTOMER")

                .requestMatchers(HttpMethod.PUT, "/rentals/*/return")
                    .hasAnyRole("CUSTOMER", "ADMIN")   
                
                .anyRequest().authenticated()
            )
            .oauth2ResourceServer(oauth2 -> oauth2
        		.jwt(jwt -> jwt
                        .jwtAuthenticationConverter(jwtAuthenticationConverter)
                    )
            );

        return http.build();
    }
}
