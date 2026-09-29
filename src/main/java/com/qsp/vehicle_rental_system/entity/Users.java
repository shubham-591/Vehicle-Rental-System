package com.qsp.vehicle_rental_system.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.fasterxml.jackson.annotation.JsonManagedReference;

@Entity
@Table(name = "users")
public class Users {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int uid;

    @NotBlank(message = "Name cannot be blank")
    @Size(min = 2, max = 50, message = "Name must contain between 2 and 50 characters")
    @Column(nullable = false, length = 50)
    private String name;

    @NotBlank(message = "Email cannot be blank")
    @Email(message = "Enter a valid email")
    @Column(nullable = false, unique = true)
    private String email;

    @NotBlank(message = "Password cannot be blank")
    @Size(min = 8, message = "Password must contain at least 8 characters")
    @Column(nullable = false)
    private String password;

//    private String role;
    @Enumerated(EnumType.STRING)
    private Role role;
    
    @OneToOne
    @JoinColumn(name = "customer_id")
    @JsonManagedReference
    private Customer customer;
    
    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

	public int getUid() {
		return uid;
	}

	public void setUid(int uid) {
		this.uid = uid;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Role getRole() {
	    return role;
	}

	public void setRole(Role role) {
	    this.role = role;
	}
	
	public Customer getCustomer() {
	    return customer;
	}

	public void setCustomer(Customer customer) {
	    this.customer = customer;
	}
}