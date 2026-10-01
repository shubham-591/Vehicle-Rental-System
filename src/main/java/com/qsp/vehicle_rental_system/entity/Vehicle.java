package com.qsp.vehicle_rental_system.entity;

import java.time.LocalDateTime;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "vehicles")
public class Vehicle {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "my_vehiclekey")
    @SequenceGenerator(
        name = "my_vehiclekey",
        sequenceName = "vehicle_sequence",
        initialValue = 301,
        allocationSize = 1
    )
    private int vid;

    @NotBlank(message = "Vehicle name cannot be blank")
    @Size(
        min = 2,
        max = 50,
        message = "Vehicle name must contain between 2 and 50 characters"
    )
    @Column(
        nullable = false,
        length = 50
    )
    private String vname;

    @DecimalMin(
        value = "0.0",
        inclusive = false,
        message = "Rent per day must be greater than 0"
    )
    @Column(nullable = false)
    private double rentPerDay;

    @NotBlank(message = "Vehicle number cannot be blank")
    @Pattern(
        regexp = "^[A-Z]{2}[0-9]{2}[A-Z]{2}[0-9]{4}$",
        message = "Vehicle number must be in format XX00XX0000"
    )
    @Column(
        nullable = false,
        unique = true,
        length = 20
    )
    private String vehicleNumber;

    @NotBlank(message = "Company cannot be blank")
    @Size(
        min = 2,
        max = 50,
        message = "Company name must contain between 2 and 50 characters"
    )
    @Column(
        nullable = false,
        length = 50
    )
    private String company;

    @Column(nullable = false)
    private boolean isAvailable = true;
    
    @Column(nullable = false)
    private boolean active = true;

	@CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @JsonManagedReference("vehicle-rentals")
    @OneToMany(mappedBy = "vehicle")
    private List<Rental> rentals;

    public List<Rental> getRentals() {
        return rentals;
    }

    public void setRentals(List<Rental> rentals) {
        this.rentals = rentals;
    }

    public int getVid() {
        return vid;
    }

    public void setVid(int vid) {
        this.vid = vid;
    }

    public String getVname() {
        return vname;
    }

    public void setVname(String vname) {
        this.vname = vname;
    }

    public double getRentPerDay() {
        return rentPerDay;
    }

    public void setRentPerDay(double rentPerDay) {
        this.rentPerDay = rentPerDay;
    }

    public String getVehicleNumber() {
        return vehicleNumber;
    }

    public void setVehicleNumber(String vehicleNumber) {
        this.vehicleNumber = vehicleNumber;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public boolean isAvailable() {
        return isAvailable;
    }

    public void setAvailable(boolean isAvailable) {
        this.isAvailable = isAvailable;
    }
    
    public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public LocalDateTime getUpdatedAt() {
		return updatedAt;
	}
	
}
