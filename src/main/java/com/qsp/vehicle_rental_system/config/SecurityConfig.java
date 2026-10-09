package com.qsp.vehicle_rental_system.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;

import java.util.List;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
public class SecurityConfig {
	
	private final JwtAuthenticationConverter jwtAuthenticationConverter;

    public SecurityConfig(JwtAuthenticationConverter jwtAuthenticationConverter) {
        this.jwtAuthenticationConverter = jwtAuthenticationConverter;
    }
    
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http)
            throws Exception {

        http
        	.cors(cors -> {})
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/auth/register", "/auth/login").permitAll()
                
                // Customer APIs
                .requestMatchers(HttpMethod.GET, "/customers", "/customers/**")
                .hasRole("ADMIN")

                .requestMatchers(HttpMethod.DELETE, "/customers/**")
                .hasRole("ADMIN")
                
                .requestMatchers(HttpMethod.PATCH, "/customers/**")
                .hasAnyRole("ADMIN", "CUSTOMER")
                
                // Vehicle APIs
                .requestMatchers(HttpMethod.POST, "/vehicles")
                    .hasRole("ADMIN")
                .requestMatchers(HttpMethod.GET, "/vehicles", "/vehicles/**")
                    .hasAnyRole("CUSTOMER", "ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/vehicles/**")
                    .hasRole("ADMIN")
                
                .requestMatchers(HttpMethod.PATCH, "/vehicles/**")
                .hasRole("ADMIN")
                    
                // Rental APIs
                .requestMatchers(HttpMethod.POST, "/rentals")
                    .hasAnyRole("CUSTOMER")

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
    
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        configuration.setAllowedOrigins(
            List.of("http://localhost:5173")
        );

        configuration.setAllowedMethods(
            List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS")
        );

        configuration.setAllowedHeaders(
            List.of("Authorization", "Content-Type")
        );

        UrlBasedCorsConfigurationSource source =
            new UrlBasedCorsConfigurationSource();

        source.registerCorsConfiguration("/**", configuration);

        return source;
    }
}
